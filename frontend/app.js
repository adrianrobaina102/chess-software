// Mapeo de caracteres FEN a símbolos Unicode de ajedrez
const PIECE_SYMBOLS = {
    'K': '♔', 'Q': '♕', 'R': '♖', 'B': '♗', 'N': '♘', 'P': '♙',
    'k': '♚', 'q': '♛', 'r': '♜', 'b': '♝', 'n': '♞', 'p': '♟'
};

const FILES = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'];

class ChessGameClient {
    constructor() {
        this.boardElement = document.getElementById('board');
        this.fenInput = document.getElementById('fen-display');
        this.turnIndicator = document.getElementById('turn-indicator');
        this.resetButton = document.getElementById('reset-btn');

        this.currentFen = '';
        this.selectedSquare = null;
        this.availableMoves = new Set();
        this.gameStatus = 'Playing';
        this.checkedKingSquare = null;

        this.init();
    }

    async init() {
        this.resetButton.addEventListener('click', () => this.fetchInitialBoard());
        await this.fetchInitialBoard();
    }

    async fetchInitialBoard() {
        try {
            const response = await fetch('/api/board/initial');
            this.currentFen = await response.text();
            this.selectedSquare = null;
            this.availableMoves.clear();
            this.gameStatus = 'Playing';
            this.checkedKingSquare = null;
            this.updateUi();
            this.turnIndicator.textContent = `Turno: ${this.currentTurnLabel()}`;
        } catch (error) {
            console.error('Error fetching initial board:', error);
            this.turnIndicator.textContent = 'Error conectando con el backend';
        }
    }

    async fetchGameStatus() {
        const url = `/api/board/status?board=${encodeURIComponent(this.currentFen)}`;
        try {
            const response = await fetch(url);
            const data = await response.json();
            this.gameStatus = data?.status || 'Playing';
            this.checkedKingSquare = data?.kingSquare || null;
        } catch (error) {
            console.error('Error fetching game status:', error);
            this.gameStatus = 'Playing';
            this.checkedKingSquare = null;
        }
    }

    async fetchAvailableMoves(squareName) {
        const url = `/api/board/moves?board=${encodeURIComponent(this.currentFen)}&square=${encodeURIComponent(squareName)}`;
        try {
            const response = await fetch(url);
            const moves = await response.json();
            this.availableMoves = new Set(Array.isArray(moves) ? moves : []);
            this.renderBoard();
        } catch (error) {
            console.error('Error fetching available moves:', error);
            this.availableMoves.clear();
            this.renderBoard();
        }
    }

    async makeMove(fromSquare, toSquare) {
        const uciMove = `${fromSquare}${toSquare}`;
        const url = `/api/board/move?board=${encodeURIComponent(this.currentFen)}&move=${encodeURIComponent(uciMove)}`;

        try {
            const response = await fetch(url);
            const newFen = await response.text();
            const moved = newFen !== this.currentFen;
            this.currentFen = newFen;
            this.selectedSquare = null;
            this.availableMoves.clear();
            if (moved) {
                await this.fetchGameStatus();
            }
            this.updateUi();
            this.updateStatusMessage(moved, uciMove);
        } catch (error) {
            console.error('Error applying move:', error);
            this.turnIndicator.textContent = 'Error al enviar jugada';
        }
    }

    updateUi() {
        this.fenInput.value = this.currentFen;
        this.renderBoard();
    }

    renderBoard() {
        this.boardElement.innerHTML = '';
        const matrix = this.parseFenToMatrix(this.currentFen);

        for (let row = 0; row < 8; row++) {
            const rank = 8 - row;
            for (let col = 0; col < 8; col++) {
                const file = FILES[col];
                const squareName = `${file}${rank}`;
                const piece = matrix[row][col];

                const squareDiv = document.createElement('div');
                const isLight = (row + col) % 2 === 0;
                squareDiv.className = `square ${isLight ? 'light' : 'dark'}`;
                squareDiv.dataset.square = squareName;

                if (this.selectedSquare === squareName) {
                    squareDiv.classList.add('selected');
                }
                if (this.availableMoves.has(squareName)) {
                    squareDiv.classList.add(piece ? 'available-capture' : 'available-move');
                }
                if (this.checkedKingSquare === squareName) {
                    squareDiv.classList.add('in-check');
                }

                if (piece) {
                    const pieceSpan = document.createElement('span');
                    pieceSpan.className = 'piece';
                    pieceSpan.textContent = PIECE_SYMBOLS[piece] || piece;
                    squareDiv.appendChild(pieceSpan);
                }

                if (col === 0) {
                    const rankLabel = document.createElement('span');
                    rankLabel.className = 'coord-rank';
                    rankLabel.textContent = rank;
                    squareDiv.appendChild(rankLabel);
                }

                if (row === 7) {
                    const fileLabel = document.createElement('span');
                    fileLabel.className = 'coord-file';
                    fileLabel.textContent = file;
                    squareDiv.appendChild(fileLabel);
                }

                squareDiv.addEventListener('click', () => this.onSquareClicked(squareName, piece));
                this.boardElement.appendChild(squareDiv);
            }
        }
    }

    onSquareClicked(squareName, piece) {
        if (this.isGameOver()) {
            return;
        }
        if (!this.selectedSquare) {
            // Primer clic: seleccionar casilla de origen si tiene pieza del turno activo
            if (piece) {
                if (!this.isCurrentTurnPiece(piece)) {
                    this.turnIndicator.textContent = `No es su turno · Turno: ${this.currentTurnLabel()}`;
                    return;
                }
                this.selectedSquare = squareName;
                this.turnIndicator.textContent = `Casilla origen: ${squareName} · Turno: ${this.currentTurnLabel()}`;
                this.renderBoard();
                this.fetchAvailableMoves(squareName);
            }
        } else {
            // Segundo clic: destino
            if (this.selectedSquare === squareName) {
                // Deseleccionar al hacer clic en la misma
                this.selectedSquare = null;
                this.availableMoves.clear();
                this.turnIndicator.textContent = `Selección cancelada · Turno: ${this.currentTurnLabel()}`;
                this.renderBoard();
            } else {
                const from = this.selectedSquare;
                const to = squareName;
                this.makeMove(from, to);
            }
        }
    }

    currentTurnSide() {
        const side = this.currentFen.split(' ')[1];
        return side === 'b' ? 'b' : 'w';
    }

    currentTurnLabel() {
        return this.currentTurnSide() === 'b' ? 'Negras' : 'Blancas';
    }

    isCurrentTurnPiece(piece) {
        const isWhitePiece = piece === piece.toUpperCase();
        return this.currentTurnSide() === 'w' ? isWhitePiece : !isWhitePiece;
    }

    isGameOver() {
        return this.gameStatus === 'Checkmate' || this.gameStatus === 'Stalemate';
    }

    updateStatusMessage(moved, uciMove) {
        if (!moved) {
            this.turnIndicator.textContent = `Movimiento inválido: ${uciMove} · Turno: ${this.currentTurnLabel()}`;
            return;
        }
        if (this.gameStatus === 'Checkmate') {
            const winner = this.currentTurnSide() === 'w' ? 'Negras' : 'Blancas';
            this.turnIndicator.textContent = `¡Jaque mate! Ganan ${winner}`;
            return;
        }
        if (this.gameStatus === 'Stalemate') {
            this.turnIndicator.textContent = 'Tablas por ahogado';
            return;
        }
        if (this.gameStatus === 'Check') {
            this.turnIndicator.textContent = `¡Jaque! · Turno: ${this.currentTurnLabel()}`;
            return;
        }
        this.turnIndicator.textContent = `Movimiento realizado: ${uciMove} · Turno: ${this.currentTurnLabel()}`;
    }

    parseFenToMatrix(fen) {
        const matrix = Array.from({ length: 8 }, () => Array(8).fill(null));
        const boardPart = fen.split(' ')[0];
        const ranks = boardPart.split('/');

        for (let row = 0; row < 8; row++) {
            const rankStr = ranks[row] || '';
            let col = 0;
            for (const char of rankStr) {
                if (char >= '1' && char <= '8') {
                    col += parseInt(char, 10);
                } else {
                    matrix[row][col] = char;
                    col++;
                }
            }
        }
        return matrix;
    }
}

// Iniciar cliente al cargar la ventana
window.addEventListener('DOMContentLoaded', () => new ChessGameClient());

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
            this.updateUi();
        } catch (error) {
            console.error('Error fetching initial board:', error);
            this.turnIndicator.textContent = 'Error conectando con el backend';
        }
    }

    async makeMove(fromSquare, toSquare) {
        const uciMove = `${fromSquare}${toSquare}`;
        const url = `/api/board/move?board=${encodeURIComponent(this.currentFen)}&move=${encodeURIComponent(uciMove)}`;

        try {
            const response = await fetch(url);
            const newFen = await response.text();
            this.currentFen = newFen;
            this.selectedSquare = null;
            this.updateUi();
            this.turnIndicator.textContent = `Movimiento realizado: ${uciMove}`;
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

                if (piece) {
                    squareDiv.textContent = PIECE_SYMBOLS[piece] || piece;
                }

                squareDiv.addEventListener('click', () => this.onSquareClicked(squareName, piece));
                this.boardElement.appendChild(squareDiv);
            }
        }
    }

    onSquareClicked(squareName, piece) {
        if (!this.selectedSquare) {
            // Primer clic: seleccionar casilla de origen si tiene pieza
            if (piece) {
                this.selectedSquare = squareName;
                this.turnIndicator.textContent = `Casilla origen: ${squareName}. Selecciona destino`;
                this.renderBoard();
            }
        } else {
            // Segundo clic: destino
            if (this.selectedSquare === squareName) {
                // Deseleccionar al hacer clic en la misma
                this.selectedSquare = null;
                this.turnIndicator.textContent = 'Selección cancelada';
                this.renderBoard();
            } else {
                const from = this.selectedSquare;
                const to = squareName;
                this.makeMove(from, to);
            }
        }
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

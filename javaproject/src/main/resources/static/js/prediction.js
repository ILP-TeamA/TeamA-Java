// Prediction Page JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // 日付入力の初期化
    initializeDateInput();
    
    // 数量入力フィールドにフォーカス時のエフェクト
    setupQuantityInputEffects();
    
    // フォームバリデーション
    setupFormValidation();
});

/**
 * 日付入力の初期化
 */
function initializeDateInput() {
    const dateInput = document.getElementById('prediction-date');
    if (dateInput) {
        // 日付制限は設定せず、元のロジック通り任意の日付を選択可能にする
        // デフォルト値も設定しない（ユーザーが自由に選択）
        
        // 今日の日付をデフォルトで設定する場合（コメントアウト）
        // const today = Utils.getTodayString();
        // dateInput.value = today;
        
        // 日付変更時の処理
        dateInput.addEventListener('change', function() {
            validateDateInput(this);
        });
    }
}

/**
 * 日付入力の検証
 */
function validateDateInput(dateInput) {
    const selectedDate = new Date(dateInput.value);
    const today = new Date();
    
    // 過去すぎる日付の警告（1年以上前）
    const oneYearAgo = new Date();
    oneYearAgo.setFullYear(today.getFullYear() - 1);
    
    if (selectedDate < oneYearAgo) {
        Message.showWarning('選択された日付が古すぎます。予測精度が低下する可能性があります。');
    }
    
    // 未来すぎる日付の警告（1年以上先）
    const oneYearLater = new Date();
    oneYearLater.setFullYear(today.getFullYear() + 1);
    
    if (selectedDate > oneYearLater) {
        Message.showWarning('選択された日付が遠すぎます。予測精度が低下する可能性があります。');
    }
}

/**
 * 数量入力フィールドのエフェクト設定
 */
function setupQuantityInputEffects() {
    const quantityInputs = document.querySelectorAll('.quantity-input');
    quantityInputs.forEach(input => {
        input.addEventListener('focus', function() {
            this.style.transform = 'scale(1.05)';
            this.style.boxShadow = '0 0 0 3px rgba(102, 126, 234, 0.2)';
        });
        
        input.addEventListener('blur', function() {
            this.style.transform = 'scale(1)';
            this.style.boxShadow = '';
        });
        
        // 数値入力の検証
        input.addEventListener('input', function() {
            validateQuantityInput(this);
        });
    });
}

/**
 * 数量入力の検証
 */
function validateQuantityInput(input) {
    const value = parseInt(input.value);
    
    if (isNaN(value) || value < 0) {
        input.style.borderColor = 'var(--danger-color)';
        showInputError(input, '0以上の数値を入力してください');
    } else if (value > 9999) {
        input.style.borderColor = 'var(--warning-color)';
        showInputError(input, '数量が大きすぎます（最大9999）');
    } else {
        input.style.borderColor = '';
        hideInputError(input);
    }
}

/**
 * 入力エラー表示
 */
function showInputError(input, message) {
    hideInputError(input);
    
    const errorSpan = document.createElement('span');
    errorSpan.className = 'input-error';
    errorSpan.style.cssText = `
        color: var(--danger-color);
        font-size: var(--font-xs);
        position: absolute;
        top: 100%;
        left: 0;
        white-space: nowrap;
    `;
    errorSpan.textContent = message;
    
    input.parentNode.style.position = 'relative';
    input.parentNode.appendChild(errorSpan);
}

/**
 * 入力エラー非表示
 */
function hideInputError(input) {
    const existingError = input.parentNode.querySelector('.input-error');
    if (existingError) {
        existingError.remove();
    }
}

/**
 * フォームバリデーション設定
 */
function setupFormValidation() {
    const confirmForm = document.querySelector('form[action="/prediction/confirmation"]');
    if (confirmForm) {
        confirmForm.addEventListener('submit', function(e) {
            if (!validateConfirmForm()) {
                e.preventDefault();
            }
        });
    }
}

/**
 * 確認フォームの検証
 */
function validateConfirmForm() {
    const quantityInputs = document.querySelectorAll('.quantity-input');
    let hasValidInput = false;
    let allValid = true;
    
    quantityInputs.forEach(input => {
        const value = parseInt(input.value);
        
        if (isNaN(value) || value < 0) {
            input.style.borderColor = 'var(--danger-color)';
            allValid = false;
        } else {
            input.style.borderColor = '';
            if (value > 0) {
                hasValidInput = true;
            }
        }
    });
    
    if (!allValid) {
        Message.showError('入力値に誤りがあります。0以上の数値を入力してください。');
        return false;
    }
    
    if (!hasValidInput) {
        if (confirm('全ての数量が0です。このまま確認画面に進みますか？')) {
            return true;
        } else {
            return false;
        }
    }
    
    return true;
}

/**
 * 予測実行
 */
function runPrediction() {
    const date = document.getElementById('prediction-date').value;
    if (!date) {
        Message.showError('日付を選択してください！');
        document.getElementById('prediction-date').focus();
        return;
    }

    // ローディング表示
    const button = document.querySelector('button[onclick="runPrediction()"]');
    if (button) {
        Loading.setButton(button, true, button.innerHTML);
    }

    // フォームを作成して送信
    const form = document.createElement('form');
    form.method = 'post';
    form.action = '/prediction/run';

    const input = document.createElement('input');
    input.type = 'hidden';
    input.name = 'date';
    input.value = date;

    form.appendChild(input);
    document.body.appendChild(form);
    form.submit();
}

/**
 * 予測データの可視化（予測結果がある場合）
 */
function visualizePredictionData() {
    const predictionTable = document.querySelector('.prediction-table');
    if (!predictionTable) return;
    
    const rows = predictionTable.querySelectorAll('tbody tr');
    
    rows.forEach((row, index) => {
        // 行のアニメーション
        row.style.opacity = '0';
        row.style.transform = 'translateX(-20px)';
        
        setTimeout(() => {
            row.style.transition = 'all 0.3s ease';
            row.style.opacity = '1';
            row.style.transform = 'translateX(0)';
        }, index * 100);
        
        // 予測数量に基づく視覚的フィードバック
        const quantityCell = row.querySelector('.predicted-quantity');
        if (quantityCell) {
            const quantity = parseInt(quantityCell.textContent);
            
            if (quantity > 200) {
                quantityCell.style.backgroundColor = '#ffebee';
                quantityCell.style.color = '#c62828';
            } else if (quantity > 100) {
                quantityCell.style.backgroundColor = '#fff3e0';
                quantityCell.style.color = '#ef6c00';
            } else {
                quantityCell.style.backgroundColor = '#e8f5e8';
                quantityCell.style.color = '#2e7d32';
            }
        }
    });
}

/**
 * 数量入力の自動計算（予測値に基づく推奨値）
 */
function suggestQuantities() {
    const quantityInputs = document.querySelectorAll('.quantity-input');
    
    quantityInputs.forEach(input => {
        const row = input.closest('tr');
        const predictedCell = row.querySelector('.predicted-quantity');
        
        if (predictedCell) {
            const predictedQuantity = parseInt(predictedCell.textContent);
            
            // 予測値の110%を推奨値として設定
            const suggestedQuantity = Math.ceil(predictedQuantity * 1.1);
            
            // プレースホルダーに推奨値を表示
            input.placeholder = `推奨: ${suggestedQuantity}本`;
            
            // ダブルクリックで推奨値を自動入力
            input.addEventListener('dblclick', function() {
                this.value = suggestedQuantity;
                this.style.backgroundColor = '#e3f2fd';
                
                setTimeout(() => {
                    this.style.backgroundColor = '';
                }, 1000);
                
                Message.showInfo(`推奨値 ${suggestedQuantity}本 を入力しました`);
            });
        }
    });
}

/**
 * キーボードショートカット
 */
function setupKeyboardShortcuts() {
    document.addEventListener('keydown', function(e) {
        // Ctrl + Enter で予測実行
        if (e.ctrlKey && e.key === 'Enter') {
            e.preventDefault();
            runPrediction();
        }
        
        // Escape でフォーカス解除
        if (e.key === 'Escape') {
            document.activeElement.blur();
        }
    });
}

// 追加の初期化
document.addEventListener('DOMContentLoaded', function() {
    visualizePredictionData();
    suggestQuantities();
    setupKeyboardShortcuts();
});
// Account Edit JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // パスワード編集画面でのみ実行
    if (document.getElementById('newValue') && document.getElementById('confirmValue')) {
        setupPasswordValidation();
    }
    
    // アカウント削除の確認
    setupDeleteConfirmation();
});

/**
 * パスワード確認の検証
 */
function setupPasswordValidation() {
    const newPassword = document.getElementById('newValue');
    const confirmPassword = document.getElementById('confirmValue');
    
    if (!newPassword || !confirmPassword) return;
    
    function validatePasswords() {
        if (newPassword.value && confirmPassword.value) {
            if (newPassword.value !== confirmPassword.value) {
                confirmPassword.setCustomValidity('パスワードが一致しません');
                confirmPassword.style.borderColor = 'var(--danger-color)';
            } else {
                confirmPassword.setCustomValidity('');
                confirmPassword.style.borderColor = 'var(--success-color)';
            }
        } else {
            confirmPassword.setCustomValidity('');
            confirmPassword.style.borderColor = '';
        }
    }
    
    newPassword.addEventListener('input', validatePasswords);
    confirmPassword.addEventListener('input', validatePasswords);
    
    // フォーム送信時の最終チェック
    const form = newPassword.closest('form');
    if (form) {
        form.addEventListener('submit', function(e) {
            if (newPassword.value !== confirmPassword.value) {
                e.preventDefault();
                Message.showError('パスワードが一致しません。再度確認してください。');
            }
        });
    }
}

/**
 * アカウント削除確認
 */
function setupDeleteConfirmation() {
    const deleteButtons = document.querySelectorAll('.delete-btn');
    deleteButtons.forEach(button => {
        button.addEventListener('click', confirmDelete);
    });
}

/**
 * 削除確認ダイアログ
 */
function confirmDelete(event) {
    event.preventDefault();
    
    const username = document.querySelector('.user-info-badge span')?.textContent || 'このアカウント';
    
    if (confirm(`本当に「${username}」を削除しますか？\n\nこの操作は取り消せません。\n削除されたアカウントは復元できません。`)) {
        // 削除フォームを動的に作成して送信
        const userId = event.target.dataset.userId || 
                      event.target.closest('[data-user-id]')?.dataset.userId;
        
        if (userId) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '/account/delete/' + userId;
            
            // CSRFトークンがある場合は追加
            const csrfToken = document.querySelector('meta[name="_csrf"]');
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]');
            if (csrfToken && csrfHeader) {
                const csrfInput = document.createElement('input');
                csrfInput.type = 'hidden';
                csrfInput.name = '_csrf';
                csrfInput.value = csrfToken.content;
                form.appendChild(csrfInput);
            }
            
            document.body.appendChild(form);
            form.submit();
        } else {
            Message.showError('ユーザーIDが見つかりません。');
        }
    }
}

/**
 * フィールド編集の共通処理
 */
function setupFieldEdit() {
    const editForm = document.querySelector('form[action*="/account/edit/"]');
    if (!editForm) return;
    
    editForm.addEventListener('submit', function(e) {
        const newValue = document.getElementById('newValue');
        if (!newValue) return;
        
        // 空値チェック
        if (!newValue.value.trim()) {
            e.preventDefault();
            Message.showError('入力値が空です。値を入力してください。');
            newValue.focus();
            return;
        }
        
        // メール形式チェック（メール編集の場合）
        if (newValue.type === 'email' && !Validator.isValidEmail(newValue.value)) {
            e.preventDefault();
            Message.showError('有効なメールアドレス形式で入力してください。');
            newValue.focus();
            return;
        }
        
        // パスワード長チェック（パスワード編集の場合）
        if (newValue.type === 'password' && !Validator.isValidPassword(newValue.value)) {
            e.preventDefault();
            Message.showError('パスワードは6文字以上で入力してください。');
            newValue.focus();
            return;
        }
        
        // 送信前の確認
        const editType = document.querySelector('h1 span')?.textContent || '項目';
        if (!confirm(`${editType}を変更しますか？`)) {
            e.preventDefault();
        }
    });
}

/**
 * 入力フィールドの動的バリデーション
 */
function setupDynamicValidation() {
    const inputs = document.querySelectorAll('.form-control');
    
    inputs.forEach(input => {
        input.addEventListener('blur', function() {
            validateField(this);
        });
        
        input.addEventListener('input', function() {
            // リアルタイムでエラー表示をクリア
            if (this.style.borderColor === 'var(--danger-color)') {
                this.style.borderColor = '';
            }
        });
    });
}

/**
 * 個別フィールドバリデーション
 */
function validateField(field) {
    let isValid = true;
    let errorMessage = '';
    
    if (field.hasAttribute('required') && !field.value.trim()) {
        isValid = false;
        errorMessage = 'この項目は必須です。';
    } else if (field.type === 'email' && field.value && !Validator.isValidEmail(field.value)) {
        isValid = false;
        errorMessage = '有効なメールアドレス形式で入力してください。';
    } else if (field.type === 'password' && field.value && !Validator.isValidPassword(field.value)) {
        isValid = false;
        errorMessage = 'パスワードは6文字以上で入力してください。';
    }
    
    if (!isValid) {
        field.style.borderColor = 'var(--danger-color)';
        showFieldError(field, errorMessage);
    } else {
        field.style.borderColor = '';
        hideFieldError(field);
    }
    
    return isValid;
}

/**
 * フィールドエラー表示
 */
function showFieldError(field, message) {
    hideFieldError(field); // 既存のエラーを削除
    
    const errorDiv = document.createElement('div');
    errorDiv.className = 'field-error';
    errorDiv.style.cssText = `
        color: var(--danger-color);
        font-size: var(--font-sm);
        margin-top: 4px;
        display: flex;
        align-items: center;
        gap: 4px;
    `;
    errorDiv.innerHTML = `<i class="fas fa-exclamation-circle"></i> ${message}`;
    
    field.parentNode.appendChild(errorDiv);
}

/**
 * フィールドエラー非表示
 */
function hideFieldError(field) {
    const existingError = field.parentNode.querySelector('.field-error');
    if (existingError) {
        existingError.remove();
    }
}

// 初期化
document.addEventListener('DOMContentLoaded', function() {
    setupFieldEdit();
    setupDynamicValidation();
});
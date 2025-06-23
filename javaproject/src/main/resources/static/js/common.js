// Common JavaScript Functions

/**
 * 共通ユーティリティ関数
 */
const Utils = {
    /**
     * 通貨フォーマット
     */
    formatCurrency: function(amount) {
        if (typeof amount !== 'number') {
            amount = parseInt(amount) || 0;
        }
        return '¥' + amount.toLocaleString();
    },

    /**
     * 数値フォーマット
     */
    formatNumber: function(number) {
        if (typeof number !== 'number') {
            number = parseInt(number) || 0;
        }
        return number.toLocaleString();
    },

    /**
     * 日付フォーマット
     */
    formatDate: function(date, format = 'yyyy/MM/dd') {
        if (typeof date === 'string') {
            date = new Date(date);
        }
        
        if (!(date instanceof Date) || isNaN(date)) {
            return '';
        }
        
        const yyyy = date.getFullYear();
        const mm = String(date.getMonth() + 1).padStart(2, '0');
        const dd = String(date.getDate()).padStart(2, '0');
        
        return format
            .replace('yyyy', yyyy)
            .replace('MM', mm)
            .replace('dd', dd);
    },

    /**
     * 今日の日付を取得（yyyy-mm-dd形式）
     */
    getTodayString: function() {
        const today = new Date();
        const yyyy = today.getFullYear();
        const mm = String(today.getMonth() + 1).padStart(2, '0');
        const dd = String(today.getDate()).padStart(2, '0');
        return `${yyyy}-${mm}-${dd}`;
    },

    /**
     * 文字列が空かどうかチェック
     */
    isEmpty: function(str) {
        return !str || str.trim().length === 0;
    },

    /**
     * 数値かどうかチェック
     */
    isNumber: function(value) {
        return !isNaN(value) && !isNaN(parseFloat(value));
    },

    /**
     * 配列をシャッフル
     */
    shuffleArray: function(array) {
        const newArray = [...array];
        for (let i = newArray.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [newArray[i], newArray[j]] = [newArray[j], newArray[i]];
        }
        return newArray;
    },

    /**
     * 文字列を省略
     */
    truncate: function(str, length = 50) {
        if (!str || str.length <= length) return str;
        return str.substring(0, length) + '...';
    }
};

/**
 * メッセージ表示関連
 */
const Message = {
    /**
     * 成功メッセージ表示
     */
    showSuccess: function(message, duration = 5000) {
        this.show(message, 'success', duration);
    },

    /**
     * エラーメッセージ表示
     */
    showError: function(message, duration = 5000) {
        this.show(message, 'error', duration);
    },

    /**
     * 警告メッセージ表示
     */
    showWarning: function(message, duration = 5000) {
        this.show(message, 'warning', duration);
    },

    /**
     * 情報メッセージ表示
     */
    showInfo: function(message, duration = 5000) {
        this.show(message, 'info', duration);
    },

    /**
     * メッセージ表示の共通関数
     */
    show: function(message, type = 'info', duration = 5000) {
        this.clear();

        const messageDiv = document.createElement('div');
        messageDiv.className = `message message-${type}`;
        messageDiv.setAttribute('id', 'common-message');

        const icon = this.getIcon(type);
        messageDiv.innerHTML = `${icon} ${message}`;

        // メッセージを挿入する場所を決定
        const container = document.querySelector('.container') || document.body;
        let insertBefore = container.firstChild;

        // より適切な挿入位置を探す
        const header = document.querySelector('.card-header, .header, h1');
        if (header && header.nextSibling) {
            insertBefore = header.nextSibling;
        }

        container.insertBefore(messageDiv, insertBefore);

        // アニメーション
        messageDiv.style.opacity = '0';
        messageDiv.style.transform = 'translateY(-10px)';
        
        setTimeout(() => {
            messageDiv.style.transition = 'all 0.3s ease';
            messageDiv.style.opacity = '1';
            messageDiv.style.transform = 'translateY(0)';
        }, 10);

        // 自動削除
        if (duration > 0) {
            setTimeout(() => {
                this.fadeOut(messageDiv);
            }, duration);
        }
    },

    /**
     * メッセージクリア
     */
    clear: function() {
        const existingMessage = document.getElementById('common-message');
        if (existingMessage) {
            this.fadeOut(existingMessage);
        }
    },

    /**
     * フェードアウト
     */
    fadeOut: function(element) {
        if (!element || !element.parentNode) return;
        
        element.style.transition = 'all 0.3s ease';
        element.style.opacity = '0';
        element.style.transform = 'translateY(-10px)';
        
        setTimeout(() => {
            if (element.parentNode) {
                element.remove();
            }
        }, 300);
    },

    /**
     * タイプ別アイコン取得
     */
    getIcon: function(type) {
        const icons = {
            success: '<i class="fas fa-check-circle"></i>',
            error: '<i class="fas fa-exclamation-triangle"></i>',
            warning: '<i class="fas fa-exclamation-circle"></i>',
            info: '<i class="fas fa-info-circle"></i>'
        };
        return icons[type] || icons.info;
    }
};

/**
 * ローディング表示関連
 */
const Loading = {
    /**
     * ボタンのローディング状態を設定
     */
    setButton: function(button, loading = true, originalText = null) {
        if (typeof button === 'string') {
            button = document.querySelector(button);
        }
        
        if (!button) return;

        if (loading) {
            if (!originalText) {
                originalText = button.innerHTML;
            }
            button.dataset.originalText = originalText;
            button.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 処理中...';
            button.disabled = true;
            button.classList.add('btn-loading');
        } else {
            const text = button.dataset.originalText || 'ボタン';
            button.innerHTML = text;
            button.disabled = false;
            button.classList.remove('btn-loading');
            delete button.dataset.originalText;
        }
    },

    /**
     * 全画面ローディング表示
     */
    show: function(message = '読み込み中...') {
        this.hide(); // 既存のローディングを削除

        const loadingDiv = document.createElement('div');
        loadingDiv.id = 'common-loading';
        loadingDiv.innerHTML = `
            <div class="loading-overlay">
                <div class="loading-content">
                    <i class="fas fa-spinner fa-spin fa-2x"></i>
                    <p>${message}</p>
                </div>
            </div>
        `;

        // CSS スタイルを追加
        const style = document.createElement('style');
        style.id = 'loading-styles';
        style.textContent = `
            #common-loading .loading-overlay {
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: rgba(0, 0, 0, 0.5);
                display: flex;
                justify-content: center;
                align-items: center;
                z-index: 9999;
                backdrop-filter: blur(2px);
            }
            #common-loading .loading-content {
                background: white;
                padding: 30px;
                border-radius: 15px;
                text-align: center;
                box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
                min-width: 200px;
            }
            #common-loading .loading-content i {
                color: var(--primary-color, #667eea);
                margin-bottom: 15px;
            }
            #common-loading .loading-content p {
                margin: 0;
                color: #333;
                font-weight: 600;
                font-size: 16px;
            }
        `;

        document.head.appendChild(style);
        document.body.appendChild(loadingDiv);

        // アニメーション
        loadingDiv.style.opacity = '0';
        setTimeout(() => {
            loadingDiv.style.transition = 'opacity 0.3s ease';
            loadingDiv.style.opacity = '1';
        }, 10);
    },

    /**
     * 全画面ローディング非表示
     */
    hide: function() {
        const loading = document.getElementById('common-loading');
        const styles = document.getElementById('loading-styles');
        
        if (loading) {
            loading.style.opacity = '0';
            setTimeout(() => {
                loading.remove();
            }, 300);
        }
        
        if (styles) {
            styles.remove();
        }
    }
};

/**
 * バリデーション関連
 */
const Validator = {
    /**
     * メールアドレスの検証
     */
    isValidEmail: function(email) {
        const emailRegex = /^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\.[A-Za-z]{2,})$/;
        return emailRegex.test(email);
    },

    /**
     * パスワードの検証（最低6文字）
     */
    isValidPassword: function(password) {
        return password && password.length >= 6;
    },

    /**
     * 必須フィールドの検証
     */
    isRequired: function(value) {
        return !Utils.isEmpty(value);
    },

    /**
     * 数値範囲の検証
     */
    isInRange: function(value, min, max) {
        const num = parseFloat(value);
        return !isNaN(num) && num >= min && num <= max;
    },

    /**
     * 日付の検証
     */
    isValidDate: function(dateString) {
        const date = new Date(dateString);
        return date instanceof Date && !isNaN(date);
    },

    /**
     * 郵便番号の検証（日本）
     */
    isValidPostalCode: function(postalCode) {
        const postalRegex = /^\d{3}-\d{4}$/;
        return postalRegex.test(postalCode);
    }
};

/**
 * フォーム関連ユーティリティ
 */
const Form = {
    /**
     * フォームデータを取得
     */
    getData: function(form) {
        if (typeof form === 'string') {
            form = document.querySelector(form);
        }
        
        if (!form) return {};
        
        const formData = new FormData(form);
        const data = {};
        
        for (let [key, value] of formData.entries()) {
            data[key] = value;
        }
        
        return data;
    },

    /**
     * フォームをリセット
     */
    reset: function(form) {
        if (typeof form === 'string') {
            form = document.querySelector(form);
        }
        
        if (form) {
            form.reset();
            // カスタムエラーメッセージもクリア
            const inputs = form.querySelectorAll('input, select, textarea');
            inputs.forEach(input => {
                input.setCustomValidity('');
                input.style.borderColor = '';
            });
        }
    },

    /**
     * フォームフィールドの値を設定
     */
    setValues: function(form, data) {
        if (typeof form === 'string') {
            form = document.querySelector(form);
        }
        
        if (!form || !data) return;
        
        Object.keys(data).forEach(key => {
            const field = form.querySelector(`[name="${key}"]`);
            if (field) {
                field.value = data[key];
            }
        });
    },

    /**
     * フォーム送信前の共通処理
     */
    beforeSubmit: function(form, callback) {
        if (typeof form === 'string') {
            form = document.querySelector(form);
        }
        
        if (!form) return;
        
        form.addEventListener('submit', function(e) {
            const isValid = callback ? callback(this) : true;
            if (!isValid) {
                e.preventDefault();
            }
        });
    }
};

/**
 * ストレージ関連（メモリベース）
 */
const Storage = {
    _storage: {},

    /**
     * データを保存
     */
    set: function(key, value) {
        try {
            this._storage[key] = JSON.stringify(value);
            return true;
        } catch (e) {
            console.error('Storage set error:', e);
            return false;
        }
    },

    /**
     * データを取得
     */
    get: function(key, defaultValue = null) {
        try {
            const value = this._storage[key];
            return value ? JSON.parse(value) : defaultValue;
        } catch (e) {
            console.error('Storage get error:', e);
            return defaultValue;
        }
    },

    /**
     * データを削除
     */
    remove: function(key) {
        delete this._storage[key];
    },

    /**
     * 全データをクリア
     */
    clear: function() {
        this._storage = {};
    }
};

/**
 * 共通イベントハンドラー
 */
const EventHandlers = {
    /**
     * 確認ダイアログ
     */
    confirmAction: function(message, callback) {
        if (confirm(message)) {
            if (callback && typeof callback === 'function') {
                callback();
            }
            return true;
        }
        return false;
    },

    /**
     * 外部リンクの確認
     */
    confirmExternalLink: function(url) {
        return confirm(`外部サイトに移動します。\n${url}\n\n続行しますか？`);
    },

    /**
     * ファイルダウンロード
     */
    downloadFile: function(content, filename, type = 'text/plain') {
        const blob = new Blob([content], { type });
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = filename;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
    }
};

/**
 * ページ共通初期化
 */
document.addEventListener('DOMContentLoaded', function() {
    // 自動メッセージ非表示の設定
    setupAutoHideMessages();
    
    // 外部リンクの処理
    setupExternalLinks();
    
    // フォームの共通処理
    setupCommonFormHandlers();
    
    // ツールチップの初期化
    setupTooltips();
});

/**
 * 自動メッセージ非表示の設定
 */
function setupAutoHideMessages() {
    const messages = document.querySelectorAll('.message:not(#common-message)');
    messages.forEach(message => {
        if (!message.dataset.autoHide) {
            message.dataset.autoHide = 'true';
            setTimeout(() => {
                Message.fadeOut(message);
            }, 4000);
        }
    });
}

/**
 * 外部リンクの処理
 */
function setupExternalLinks() {
    const externalLinks = document.querySelectorAll('a[href^="http"]:not([href*="' + window.location.hostname + '"])');
    externalLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            if (!EventHandlers.confirmExternalLink(this.href)) {
                e.preventDefault();
            }
        });
    });
}

/**
 * フォームの共通処理
 */
function setupCommonFormHandlers() {
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        // エンターキーでの送信を制御
        form.addEventListener('keydown', function(e) {
            if (e.key === 'Enter' && e.target.tagName !== 'TEXTAREA') {
                const submitButton = this.querySelector('button[type="submit"], input[type="submit"]');
                if (submitButton) {
                    submitButton.click();
                    e.preventDefault();
                }
            }
        });
    });
}

/**
 * ツールチップの初期化
 */
function setupTooltips() {
    const tooltipElements = document.querySelectorAll('[data-tooltip]');
    tooltipElements.forEach(element => {
        element.addEventListener('mouseenter', function() {
            showTooltip(this);
        });
        
        element.addEventListener('mouseleave', function() {
            hideTooltip();
        });
    });
}

/**
 * ツールチップ表示
 */
function showTooltip(element) {
    const tooltipText = element.dataset.tooltip;
    if (!tooltipText) return;
    
    const tooltip = document.createElement('div');
    tooltip.id = 'common-tooltip';
    tooltip.className = 'tooltip';
    tooltip.textContent = tooltipText;
    tooltip.style.cssText = `
        position: absolute;
        background: rgba(0, 0, 0, 0.8);
        color: white;
        padding: 8px 12px;
        border-radius: 4px;
        font-size: 14px;
        z-index: 10000;
        pointer-events: none;
        white-space: nowrap;
    `;
    
    document.body.appendChild(tooltip);
    
    const rect = element.getBoundingClientRect();
    tooltip.style.left = rect.left + (rect.width / 2) - (tooltip.offsetWidth / 2) + 'px';
    tooltip.style.top = rect.top - tooltip.offsetHeight - 8 + 'px';
}

/**
 * ツールチップ非表示
 */
function hideTooltip() {
    const tooltip = document.getElementById('common-tooltip');
    if (tooltip) {
        tooltip.remove();
    }
}
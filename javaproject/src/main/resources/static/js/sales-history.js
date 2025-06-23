// Sales History JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // 今日の日付をデフォルトで設定
    setDefaultDate();
    
    // 検索フォームの送信処理
    setupSearchForm();
    
    // 売上カードのエフェクト
    setupCardEffects();
    
    // メッセージの自動非表示
    setupMessageAutoHide();
    
    // キーボードショートカット
    setupKeyboardShortcuts();
    
    // 統計情報の計算
    calculateSalesStatistics();
});

/**
 * 今日の日付をデフォルトで設定
 */
function setDefaultDate() {
    const searchDateInput = document.getElementById('searchDate');
    if (searchDateInput && !searchDateInput.value) {
        searchDateInput.value = Utils.getTodayString();
    }
}

/**
 * 検索フォームの送信処理
 */
function setupSearchForm() {
    const searchForm = document.querySelector('.search-form');
    if (searchForm) {
        searchForm.addEventListener('submit', function(e) {
            const searchDate = document.getElementById('searchDate').value;
            
            if (!searchDate) {
                e.preventDefault();
                Message.showError('検索する日付を選択してください。');
                document.getElementById('searchDate').focus();
                return false;
            }
            
            // 検索実行時のローディング表示
            const submitButton = this.querySelector('button[type="submit"]');
            if (submitButton) {
                Loading.setButton(submitButton, true);
            }
        });
    }
}

/**
 * 日付検索
 */
function searchByDate() {
    const searchDate = document.getElementById('searchDate').value;
    if (!searchDate) {
        Message.showError('検索する日付を選択してください。');
        document.getElementById('searchDate').focus();
        return;
    }
    
    Loading.show('売上データを検索中...');
    
    const url = new URL('/sales/history', window.location.origin);
    url.searchParams.set('searchDate', searchDate);
    window.location.href = url.toString();
}

/**
 * 全履歴表示
 */
function showAllHistory() {
    Loading.show('全ての売上履歴を読み込み中...');
    window.location.href = '/sales/history';
}

/**
 * 売上カードのホバーエフェクト
 */
function setupCardEffects() {
    const salesCards = document.querySelectorAll('.sales-card');
    
    salesCards.forEach((card, index) => {
        // 初期アニメーション
        card.style.opacity = '0';
        card.style.transform = 'translateY(20px)';
        
        setTimeout(() => {
            card.style.transition = 'all 0.4s ease';
            card.style.opacity = '1';
            card.style.transform = 'translateY(0)';
        }, index * 100);
        
        // ホバーエフェクト
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-5px)';
            this.style.boxShadow = '0 10px 30px rgba(0, 0, 0, 0.15)';
        });
        
        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
            this.style.boxShadow = '';
        });
        
        // クリックで詳細表示
        card.addEventListener('click', function() {
            const salesDate = this.querySelector('.sales-date span').textContent;
            showSalesDetail(salesDate, this);
        });
    });
}

/**
 * 売上詳細表示
 */
function showSalesDetail(salesDate, cardElement) {
    const summaryItems = cardElement.querySelectorAll('.summary-item');
    let details = `📅 ${salesDate} の売上詳細\n\n`;
    
    summaryItems.forEach(item => {
        const label = item.querySelector('.summary-label').textContent;
        const value = item.querySelector('.summary-value').textContent;
        details += `${label}: ${value}\n`;
    });
    
    // より詳細な情報を表示する場合は、ここでAPIコールなどを行う
    details += '\n💡 カードをダブルクリックで詳細分析画面に移動';
    
    alert(details);
}

/**
 * エラーメッセージの自動非表示
 */
function setupMessageAutoHide() {
    const errorMessages = document.querySelectorAll('.message-error');
    const successMessages = document.querySelectorAll('.message-success');
    
    [...errorMessages, ...successMessages].forEach(message => {
        if (!message.dataset.autoHide) {
            message.dataset.autoHide = 'true';
            
            setTimeout(() => {
                message.style.transition = 'opacity 0.3s ease';
                message.style.opacity = '0';
                
                setTimeout(() => {
                    if (message.parentNode) {
                        message.remove();
                    }
                }, 300);
            }, 5000);
        }
    });
}

/**
 * 売上統計の計算と表示
 */
function calculateSalesStatistics() {
    const salesCards = document.querySelectorAll('.sales-card');
    if (salesCards.length === 0) return;
    
    let totalRevenue = 0;
    let totalCups = 0;
    let salesCount = 0;
    
    salesCards.forEach(card => {
        const summaryItems = card.querySelectorAll('.summary-item');
        
        summaryItems.forEach(item => {
            const label = item.querySelector('.summary-label').textContent;
            const valueText = item.querySelector('.summary-value').textContent;
            
            if (label.includes('総売上')) {
                const revenue = parseInt(valueText.replace(/[¥,]/g, '')) || 0;
                totalRevenue += revenue;
            } else if (label.includes('総販売数')) {
                const cups = parseInt(valueText.replace(/[本,]/g, '')) || 0;
                totalCups += cups;
            }
        });
        
        salesCount++;
    });
    
    if (salesCount > 1) {
        const avgRevenue = Math.round(totalRevenue / salesCount);
        const avgCups = Math.round(totalCups / salesCount);
        
        showStatistics({
            totalRevenue,
            totalCups,
            salesCount,
            avgRevenue,
            avgCups
        });
    }
}

/**
 * 統計情報表示
 */
function showStatistics(stats) {
    const existingStats = document.querySelector('.sales-statistics');
    if (existingStats) {
        existingStats.remove();
    }
    
    const statsDiv = document.createElement('div');
    statsDiv.className = 'sales-statistics';
    statsDiv.style.cssText = `
        background: linear-gradient(45deg, #667eea, #764ba2);
        color: white;
        padding: var(--spacing-md);
        border-radius: var(--radius-lg);
        margin: var(--spacing-md) 0;
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
        gap: var(--spacing-md);
        text-align: center;
        animation: fadeIn 0.5s ease;
    `;
    
    statsDiv.innerHTML = `
        <div>
            <div style="font-size: var(--font-sm); opacity: 0.9;">総売上</div>
            <div style="font-size: var(--font-lg); font-weight: bold;">¥${Utils.formatNumber(stats.totalRevenue)}</div>
        </div>
        <div>
            <div style="font-size: var(--font-sm); opacity: 0.9;">総販売数</div>
            <div style="font-size: var(--font-lg); font-weight: bold;">${Utils.formatNumber(stats.totalCups)}本</div>
        </div>
        <div>
            <div style="font-size: var(--font-sm); opacity: 0.9;">日数</div>
            <div style="font-size: var(--font-lg); font-weight: bold;">${stats.salesCount}日</div>
        </div>
        <div>
            <div style="font-size: var(--font-sm); opacity: 0.9;">日平均売上</div>
            <div style="font-size: var(--font-lg); font-weight: bold;">¥${Utils.formatNumber(stats.avgRevenue)}</div>
        </div>
        <div>
            <div style="font-size: var(--font-sm); opacity: 0.9;">日平均販売数</div>
            <div style="font-size: var(--font-lg); font-weight: bold;">${Utils.formatNumber(stats.avgCups)}本</div>
        </div>
    `;
    
    const resultsSection = document.querySelector('.results-section');
    if (resultsSection) {
        resultsSection.insertBefore(statsDiv, resultsSection.firstChild);
    }
}

/**
 * キーボードショートカット
 */
function setupKeyboardShortcuts() {
    document.addEventListener('keydown', function(e) {
        // Ctrl + F で検索フィールドにフォーカス
        if (e.ctrlKey && e.key === 'f') {
            e.preventDefault();
            const searchInput = document.getElementById('searchDate');
            if (searchInput) {
                searchInput.focus();
            }
        }
        
        // Enterで検索実行
        if (e.key === 'Enter' && document.activeElement.id === 'searchDate') {
            e.preventDefault();
            searchByDate();
        }
        
        // Ctrl + H で全履歴表示
        if (e.ctrlKey && e.key === 'h') {
            e.preventDefault();
            showAllHistory();
        }
    });
}

/**
 * エクスポート機能（将来的な機能）
 */
function exportSalesData() {
    const salesCards = document.querySelectorAll('.sales-card');
    if (salesCards.length === 0) {
        Message.showWarning('エクスポートするデータがありません。');
        return;
    }
    
    let csvContent = 'data:text/csv;charset=utf-8,';
    csvContent += '日付,曜日,総販売数,総売上\n';
    
    salesCards.forEach(card => {
        const dateElement = card.querySelector('.sales-date span');
        const dayElement = card.querySelector('.sales-date span:last-child');
        const summaryItems = card.querySelectorAll('.summary-item');
        
        const date = dateElement ? dateElement.textContent : '';
        const day = dayElement ? dayElement.textContent.replace(/[()]/g, '') : '';
        
        let cups = '';
        let revenue = '';
        
        summaryItems.forEach(item => {
            const label = item.querySelector('.summary-label').textContent;
            const value = item.querySelector('.summary-value').textContent;
            
            if (label.includes('総販売数')) {
                cups = value.replace(/[本,]/g, '');
            } else if (label.includes('総売上')) {
                revenue = value.replace(/[¥,]/g, '');
            }
        });
        
        csvContent += `${date},${day},${cups},${revenue}\n`;
    });
    
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement('a');
    link.setAttribute('href', encodedUri);
    link.setAttribute('download', `sales_history_${Utils.getTodayString()}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    
    Message.showSuccess('売上データをCSVファイルとしてダウンロードしました。');
}

/**
 * 印刷用スタイル適用
 */
function preparePrintView() {
    window.print();
}
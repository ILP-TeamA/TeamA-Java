// Sales Input JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // 今日の日付をデフォルトで設定（修正版）
    setTodayAsDefault();
    
    const inputs = document.querySelectorAll('.quantity-input');
    
    inputs.forEach(input => {
        input.addEventListener('input', updateRevenue);
    });
    
    function updateRevenue() {
        let totalRevenue = 0;
        
        inputs.forEach(input => {
            const price = parseInt(input.dataset.price);
            const quantity = parseInt(input.value) || 0;
            const revenue = price * quantity;
            
            const revenueElement = document.getElementById('revenue_' + input.id);
            if (revenueElement) {
                revenueElement.textContent = '売上額: ¥' + revenue.toLocaleString();
            }
            
            totalRevenue += revenue;
        });
        
        document.getElementById('totalRevenuePreview').textContent = 
            '本日の売上総額: ¥' + totalRevenue.toLocaleString();
    }

    // フォーム送信の処理（バリデーションのみ）
    document.getElementById('salesForm').addEventListener('submit', function(e) {
        const createBy = document.getElementById('createBy')?.value;
        const salesDate = document.getElementById('salesDate').value;
        
        if (!salesDate) {
            e.preventDefault();
            alert('販売日付を入力してください。');
            return;
        }

        // 数量が1つも入力されていない場合の確認
        let hasQuantity = false;
        inputs.forEach(input => {
            if (parseInt(input.value) > 0) {
                hasQuantity = true;
            }
        });

        if (!hasQuantity) {
            e.preventDefault();
            alert('少なくとも1つの商品の販売数量を入力してください。');
            return;
        }

        // バリデーション通過時はフォームを通常通り送信
        console.log('フォーム送信中...');
    });
});

/**
 * 今日の日付をデフォルトで設定する関数
 */
function setTodayAsDefault() {
    const salesDateInput = document.getElementById('salesDate');
    if (salesDateInput && !salesDateInput.value) {
        const today = new Date();
        const year = today.getFullYear();
        const month = String(today.getMonth() + 1).padStart(2, '0');
        const day = String(today.getDate()).padStart(2, '0');
        const todayString = `${year}-${month}-${day}`;
        
        salesDateInput.value = todayString;
        console.log('デフォルト日付設定:', todayString);
    }
}
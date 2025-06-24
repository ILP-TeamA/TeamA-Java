// Sales Weather Analysis JavaScript

// グローバル変数
let salesChart = null;

document.addEventListener('DOMContentLoaded', function() {
    // Chart.jsの初期化
    initializeSalesChart();
    
    // 日付制限の設定
    setupDateRestrictions();
    
    // ページアニメーション
    setupPageAnimations();
    
    // 天気情報の処理
    setupWeatherInteractions();
});

/**
 * Chart.jsの初期化
 */
function initializeSalesChart() {
    console.log('=== Chart初期化開始 ===');
    
    const ctx = document.getElementById('salesChart');
    if (!ctx) {
        console.error('Chart canvas element not found');
        return;
    }
    
    console.log('Canvas要素取得成功');
    
    // サーバーサイドから取得したデータ（window.salesWeatherDataに設定される）
    console.log('window.salesWeatherData:', window.salesWeatherData);
    
    const chartLabels = window.salesWeatherData?.chartLabels || null;
    let chartData = window.salesWeatherData?.chartData || null;
    
    console.log('チャートラベル:', chartLabels);
    console.log('チャートデータ(raw):', chartData);
    console.log('チャートデータのタイプ:', typeof chartData);
    
    // チャートデータの処理
    let finalChartData;
    
    try {
        if (chartData && typeof chartData === 'string') {
            console.log('文字列データをパース中...');
            // 文字列の場合はJSONパース
            finalChartData = JSON.parse(chartData);
            console.log('パース後のチャートデータ:', finalChartData);
        } else if (chartData && Array.isArray(chartData)) {
            console.log('配列データを直接使用');
            // 既に配列の場合はそのまま使用
            finalChartData = chartData;
        } else {
            console.log('データが無効、デフォルトデータを使用');
            throw new Error('チャートデータが無効です');
        }
    } catch (error) {
        console.error('チャートデータのパースエラー:', error);
        console.log('デフォルトデータを使用します');
        
        // フォールバックデータ
        finalChartData = getDefaultChartData();
    }
    
    // ラベルの処理
    const finalLabels = chartLabels || getDefaultLabels();
    
    console.log('最終ラベル:', finalLabels);
    console.log('最終チャートデータ:', finalChartData);
    
    // チャート作成
    try {
        console.log('Chart.js インスタンス作成中...');
        salesChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: finalLabels,
                datasets: finalChartData
            },
            options: getChartOptions()
        });
        
        // グローバルスコープに保存
        window.salesChart = salesChart;
        
        console.log('チャート初期化完了');
    } catch (error) {
        console.error('チャート作成エラー:', error);
        showChartError();
    }
}

/**
 * デフォルトラベルを取得
 */
function getDefaultLabels() {
    const today = new Date();
    const labels = [];
    
    for (let i = 6; i >= 0; i--) {
        const date = new Date(today);
        date.setDate(today.getDate() - i);
        // Utilsが使えない場合の代替実装
        const month = date.getMonth() + 1;
        const day = date.getDate();
        labels.push(`${month}/${day}`);
    }
    
    return labels;
}

/**
 * デフォルトチャートデータを取得
 */
function getDefaultChartData() {
    return [
        {
            label: 'ホワイトビール',
            data: [8, 12, 15, 18, 14, 11, 16],
            borderColor: '#FF6B6B',
            backgroundColor: '#FF6B6B',
            fill: false,
            tension: 0.1
        },
        {
            label: 'ラガー',
            data: [6, 10, 12, 15, 11, 9, 13],
            borderColor: '#4ECDC4',
            backgroundColor: '#4ECDC4',
            fill: false,
            tension: 0.1
        },
        {
            label: 'ペールエール',
            data: [4, 7, 9, 12, 8, 6, 10],
            borderColor: '#45B7D1',
            backgroundColor: '#45B7D1',
            fill: false,
            tension: 0.1
        },
        {
            label: 'フルーツビール',
            data: [3, 5, 7, 9, 6, 4, 8],
            borderColor: '#96CEB4',
            backgroundColor: '#96CEB4',
            fill: false,
            tension: 0.1
        },
        {
            label: '黒ビール',
            data: [2, 4, 6, 8, 5, 3, 7],
            borderColor: '#FFEAA7',
            backgroundColor: '#FFEAA7',
            fill: false,
            tension: 0.1
        },
        {
            label: 'IPA',
            data: [5, 8, 10, 13, 9, 7, 11],
            borderColor: '#DDA0DD',
            backgroundColor: '#DDA0DD',
            fill: false,
            tension: 0.1
        }
    ];
}

/**
 * チャートオプションを取得
 */
function getChartOptions() {
    return {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            title: {
                display: true,
                text: '日別ビール販売実績',
                font: {
                    size: 16
                }
            },
            legend: {
                display: false // カスタム凡例を使用
            },
            tooltip: {
                mode: 'index',
                intersect: false,
                backgroundColor: 'rgba(0, 0, 0, 0.8)',
                titleColor: 'white',
                bodyColor: 'white',
                borderColor: 'rgba(255, 255, 255, 0.1)',
                borderWidth: 1,
                callbacks: {
                    title: function(context) {
                        return `${context[0].label} の販売実績`;
                    },
                    label: function(context) {
                        return `${context.dataset.label}: ${context.parsed.y}本`;
                    },
                    afterBody: function(context) {
                        const total = context.reduce((sum, item) => sum + item.parsed.y, 0);
                        return [`合計: ${total}本`];
                    }
                }
            }
        },
        scales: {
            y: {
                beginAtZero: true,
                max: 20,
                title: {
                    display: true,
                    text: '販売本数'
                },
                grid: {
                    color: '#e0e0e0'
                },
                ticks: {
                    callback: function(value) {
                        return value + '本';
                    }
                }
            },
            x: {
                title: {
                    display: true,
                    text: '日付'
                },
                grid: {
                    color: '#e0e0e0'
                }
            }
        },
        interaction: {
            intersect: false,
            mode: 'index'
        },
        elements: {
            point: {
                radius: 4,
                hoverRadius: 6
            },
            line: {
                borderWidth: 2
            }
        },
        animation: {
            duration: 1000,
            easing: 'easeInOutQuart'
        }
    };
}

/**
 * 日付制限の設定
 */
function setupDateRestrictions() {
    const dateInput = document.getElementById('selectedDate');
    if (dateInput) {
        // 未来の日付を選択できないように制限
        const today = new Date();
        const yyyy = today.getFullYear();
        const mm = String(today.getMonth() + 1).padStart(2, '0');
        const dd = String(today.getDate()).padStart(2, '0');
        const maxDate = `${yyyy}-${mm}-${dd}`;
        dateInput.setAttribute('max', maxDate);
        
        // 日付変更時の処理
        dateInput.addEventListener('change', function() {
            const selectedDate = new Date(this.value);
            
            if (selectedDate > today) {
                if (typeof Message !== 'undefined') {
                    Message.showError('未来の日付は選択できません。');
                } else {
                    alert('未来の日付は選択できません。');
                }
                this.value = maxDate;
            } else {
                validateSelectedDate(selectedDate);
            }
        });
    }
}

/**
 * 選択日付の検証
 */
function validateSelectedDate(selectedDate) {
    const today = new Date();
    const oneYearAgo = new Date();
    oneYearAgo.setFullYear(today.getFullYear() - 1);
    
    if (selectedDate < oneYearAgo) {
        if (typeof Message !== 'undefined') {
            Message.showWarning('選択された日付が古すぎます。データが存在しない可能性があります。');
        }
    }
    
    // 週末の場合の警告
    const dayOfWeek = selectedDate.getDay();
    if (dayOfWeek === 0 || dayOfWeek === 6) {
        if (typeof Message !== 'undefined') {
            Message.showInfo('週末が選択されています。営業日のデータと異なる場合があります。');
        }
    }
}

/**
 * ページアニメーション設定
 */
function setupPageAnimations() {
    // 天気カードのアニメーション
    const weatherDays = document.querySelectorAll('.weather-day');
    weatherDays.forEach((day, index) => {
        day.style.opacity = '0';
        day.style.transform = 'translateY(20px)';
        
        setTimeout(() => {
            day.style.transition = 'all 0.4s ease';
            day.style.opacity = '1';
            day.style.transform = 'translateY(0)';
        }, index * 100);
    });
    
    // セクションのフェードイン
    const sections = document.querySelectorAll('.weather-section, .chart-section');
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('fade-in');
            }
        });
    }, {
        threshold: 0.1
    });
    
    sections.forEach(section => {
        observer.observe(section);
    });
}

/**
 * 天気情報の処理
 */
function setupWeatherInteractions() {
    const weatherDays = document.querySelectorAll('.weather-day');
    
    weatherDays.forEach(day => {
        // ホバーエフェクト
        day.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-5px) scale(1.05)';
            this.style.boxShadow = '0 8px 25px rgba(0, 0, 0, 0.15)';
        });
        
        day.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0) scale(1)';
            this.style.boxShadow = '';
        });
        
        // クリックで詳細表示
        day.addEventListener('click', function() {
            showWeatherDetail(this);
        });
    });
}

/**
 * 天気詳細表示
 */
function showWeatherDetail(weatherElement) {
    const date = weatherElement.querySelector('.weather-date')?.textContent || '';
    const temp = weatherElement.querySelector('.weather-temp')?.textContent || '';
    const wind = weatherElement.querySelector('.weather-wind')?.textContent || '';
    const icon = weatherElement.querySelector('.weather-icon')?.textContent || '';
    
    const detail = `${icon} ${date} の天気詳細\n\n` +
                   `🌡️ 気温: ${temp}\n` +
                   `💨 風速: ${wind}\n\n` +
                   `💡 この日の売上データと比較して分析できます`;
    
    alert(detail);
}

/**
 * チャートエラー表示
 */
function showChartError() {
    const chartContainer = document.querySelector('.chart-container');
    if (chartContainer) {
        chartContainer.innerHTML = `
            <div style="display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100%; color: #666;">
                <i class="fas fa-chart-line" style="font-size: 3em; margin-bottom: 1em; opacity: 0.3;"></i>
                <p>グラフデータの読み込みに失敗しました</p>
                <button onclick="location.reload()" class="btn btn-primary" style="margin-top: 1em;">
                    <i class="fas fa-refresh"></i> 再読み込み
                </button>
            </div>
        `;
    }
}

/**
 * チャートデータ更新
 */
function updateChartData(newData) {
    if (!salesChart) return;
    
    salesChart.data.datasets.forEach((dataset, index) => {
        if (newData[index]) {
            dataset.data = newData[index].data;
        }
    });
    
    salesChart.update('active');
}

/**
 * ログアウト確認
 */
function confirmLogout() {
    return confirm('ログアウトしますか？\n\n現在の分析データは保存されません。');
}

/**
 * レスポンシブ対応チャートリサイズ
 */
function handleChartResize() {
    if (salesChart) {
        salesChart.resize();
    }
}

// ウィンドウリサイズ時のチャート調整
window.addEventListener('resize', handleChartResize);
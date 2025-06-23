// Sales Weather Analysis JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // 日付制限の設定
    setupDateRestrictions();
    
    // Chart.jsの初期化
    initializeSalesChart();
    
    // ページアニメーション
    setupPageAnimations();
    
    // 天気情報の処理
    setupWeatherInteractions();
});

/**
 * 日付制限の設定
 */
function setupDateRestrictions() {
    const dateInput = document.getElementById('selectedDate');
    if (dateInput) {
        // 未来の日付を選択できないように制限
        const today = new Date();
        const maxDate = Utils.formatDate(today, 'yyyy-MM-dd');
        dateInput.setAttribute('max', maxDate);
        
        // 日付変更時の処理
        dateInput.addEventListener('change', function() {
            const selectedDate = new Date(this.value);
            
            if (selectedDate > today) {
                Message.showError('未来の日付は選択できません。');
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
        Message.showWarning('選択された日付が古すぎます。データが存在しない可能性があります。');
    }
    
    // 週末の場合の警告
    const dayOfWeek = selectedDate.getDay();
    if (dayOfWeek === 0 || dayOfWeek === 6) {
        Message.showInfo('週末が選択されています。営業日のデータと異なる場合があります。');
    }
}

/**
 * Chart.jsの初期化
 */
function initializeSalesChart() {
    const ctx = document.getElementById('salesChart');
    if (!ctx) return;
    
    // サーバーサイドからのデータを取得
    const chartLabels = window.chartLabels || getDefaultLabels();
    const chartDatasets = window.chartData || getDefaultChartData();
    
    try {
        // chartDataが文字列の場合はパース
        const datasets = typeof chartDatasets === 'string' ? 
                        JSON.parse(chartDatasets) : 
                        chartDatasets;
        
        const salesChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: chartLabels,
                datasets: datasets
            },
            options: getChartOptions()
        });
        
        // グローバルに保存（リサイズ用）
        window.salesChart = salesChart;
        
        // チャートのアニメーション完了後の処理
        salesChart.options.onComplete = function() {
            addChartInteractions(salesChart);
        };
        
    } catch (error) {
        console.error('Chart initialization error:', error);
        Message.showError('グラフの初期化に失敗しました。');
        showChartError();
    }
}

/**
 * デフォルトラベル
 */
function getDefaultLabels() {
    const today = new Date();
    const labels = [];
    
    for (let i = 6; i >= 0; i--) {
        const date = new Date(today);
        date.setDate(today.getDate() - i);
        labels.push(Utils.formatDate(date, 'M/d'));
    }
    
    return labels;
}

/**
 * デフォルトチャートデータ
 */
function getDefaultChartData() {
    return [
        {
            label: 'ホワイトビール',
            data: [120, 150, 180, 200, 170, 160, 190],
            borderColor: '#FF6B6B',
            backgroundColor: 'rgba(255, 107, 107, 0.1)',
            fill: false,
            tension: 0.1
        },
        {
            label: 'ラガー',
            data: [100, 130, 160, 180, 150, 140, 170],
            borderColor: '#4ECDC4',
            backgroundColor: 'rgba(78, 205, 196, 0.1)',
            fill: false,
            tension: 0.1
        },
        {
            label: 'ペールエール',
            data: [80, 110, 140, 160, 130, 120, 150],
            borderColor: '#45B7D1',
            backgroundColor: 'rgba(69, 183, 209, 0.1)',
            fill: false,
            tension: 0.1
        },
        {
            label: 'フルーツビール',
            data: [60, 90, 120, 140, 110, 100, 130],
            borderColor: '#96CEB4',
            backgroundColor: 'rgba(150, 206, 180, 0.1)',
            fill: false,
            tension: 0.1
        },
        {
            label: '黒ビール',
            data: [40, 70, 100, 120, 90, 80, 110],
            borderColor: '#FFEAA7',
            backgroundColor: 'rgba(255, 234, 167, 0.1)',
            fill: false,
            tension: 0.1
        },
        {
            label: 'IPA',
            data: [90, 120, 150, 170, 140, 130, 160],
            borderColor: '#DDA0DD',
            backgroundColor: 'rgba(221, 160, 221, 0.1)',
            fill: false,
            tension: 0.1
        }
    ];
}

/**
 * チャートオプション
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
                    size: 16,
                    weight: 'bold'
                },
                padding: 20
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
                max: 500,
                title: {
                    display: true,
                    text: '販売本数',
                    font: {
                        weight: 'bold'
                    }
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
                    text: '日付',
                    font: {
                        weight: 'bold'
                    }
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
                hoverRadius: 6,
                borderWidth: 2
            },
            line: {
                borderWidth: 3
            }
        },
        animation: {
            duration: 1000,
            easing: 'easeInOutQuart'
        }
    };
}

/**
 * チャートインタラクション追加
 */
function addChartInteractions(chart) {
    const canvas = chart.canvas;
    
    // クリックで詳細情報表示
    canvas.addEventListener('click', function(evt) {
        const points = chart.getElementsAtEventForMode(evt, 'nearest', { intersect: true }, true);
        
        if (points.length) {
            const firstPoint = points[0];
            const datasetIndex = firstPoint.datasetIndex;
            const index = firstPoint.index;
            const dataset = chart.data.datasets[datasetIndex];
            const value = dataset.data[index];
            const label = chart.data.labels[index];
            
            showChartPointDetail(dataset.label, label, value);
        }
    });
}

/**
 * チャートポイント詳細表示
 */
function showChartPointDetail(productName, date, value) {
    const detail = `📊 ${productName} の詳細\n\n` +
                   `📅 日付: ${date}\n` +
                   `🍺 販売数: ${value}本\n\n` +
                   `💡 このデータポイントをクリックしました`;
    
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
 * ページアニメーション
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
 * ログアウト確認
 */
function confirmLogout() {
    return confirm('ログアウトしますか？\n\n現在の分析データは保存されません。');
}

/**
 * レスポンシブ対応チャートリサイズ
 */
function handleChartResize() {
    if (window.salesChart) {
        window.salesChart.resize();
    }
}

/**
 * チャートデータのエクスポート
 */
function exportChartData() {
    if (!window.salesChart) {
        Message.showError('エクスポートできるチャートデータがありません。');
        return;
    }
    
    const chart = window.salesChart;
    let csvContent = 'data:text/csv;charset=utf-8,';
    
    // ヘッダー行
    csvContent += '日付,' + chart.data.datasets.map(dataset => dataset.label).join(',') + '\n';
    
    // データ行
    chart.data.labels.forEach((label, index) => {
        const row = [label];
        chart.data.datasets.forEach(dataset => {
            row.push(dataset.data[index] || 0);
        });
        csvContent += row.join(',') + '\n';
    });
    
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement('a');
    link.setAttribute('href', encodedUri);
    link.setAttribute('download', `sales_chart_data_${Utils.getTodayString()}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    
    Message.showSuccess('チャートデータをCSVファイルとしてダウンロードしました。');
}

/**
 * チャートの印刷
 */
function printChart() {
    if (!window.salesChart) {
        Message.showError('印刷できるチャートデータがありません。');
        return;
    }
    
    const canvas = window.salesChart.canvas;
    const dataURL = canvas.toDataURL('image/png');
    
    const printWindow = window.open('', '_blank');
    printWindow.document.write(`
        <html>
            <head>
                <title>販売実績チャート</title>
                <style>
                    body { margin: 0; padding: 20px; font-family: Arial, sans-serif; }
                    img { max-width: 100%; height: auto; }
                    h1 { text-align: center; color: #333; }
                    .date { text-align: center; color: #666; margin-bottom: 20px; }
                </style>
            </head>
            <body>
                <h1>販売実績チャート</h1>
                <div class="date">印刷日: ${Utils.formatDate(new Date())}</div>
                <img src="${dataURL}" alt="販売実績チャート" />
            </body>
        </html>
    `);
    
    printWindow.document.close();
    printWindow.print();
}

// ウィンドウリサイズ時のチャート調整
window.addEventListener('resize', handleChartResize);

// チャートの動的更新（将来的な機能）
function updateChartData(newData) {
    if (!window.salesChart) return;
    
    const chart = window.salesChart;
    chart.data.datasets.forEach((dataset, index) => {
        if (newData[index]) {
            dataset.data = newData[index].data;
        }
    });
    
    chart.update('active');
}
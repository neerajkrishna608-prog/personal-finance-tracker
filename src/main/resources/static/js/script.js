// ====================== MONTHLY OVERVIEW ======================

const financeChart = document.getElementById("financeChart");

if (financeChart) {

    new Chart(financeChart, {

        type: "line",

        data: {

            labels: months,

            datasets: [

                {
                    label: "Income",
                    data: incomeData,
                    borderColor: "#22c55e",
                    backgroundColor: "rgba(34,197,94,.20)",
                    fill: true,
                    tension: 0.4
                },

                {
                    label: "Expense",
                    data: expenseData,
                    borderColor: "#ef4444",
                    backgroundColor: "rgba(239,68,68,.15)",
                    fill: true,
                    tension: 0.4
                }

            ]

        },

        options: {

            responsive: true,
            maintainAspectRatio: false,

            plugins: {

                legend: {

                    labels: {

                        color: "white"

                    }

                }

            },

            scales: {

                x: {

                    ticks: {

                        color: "#cbd5e1"

                    },

                    grid: {

                        color: "#273449"

                    }

                },

                y: {

                    beginAtZero: true,

                    ticks: {

                        color: "#cbd5e1"

                    },

                    grid: {

                        color: "#273449"

                    }

                }

            }

        }

    });

}


// ====================== EXPENSE BREAKDOWN ======================

const expenseChart = document.getElementById("expenseChart");

if (expenseChart) {

    const totalIncome = incomeData.reduce((a, b) => a + b, 0);

    const totalExpense = expenseData.reduce((a, b) => a + b, 0);

    new Chart(expenseChart, {

        type: "doughnut",

        data: {

            labels: [

                "Income",

                "Expense"

            ],

            datasets: [

                {

                    data: [

                        totalIncome,

                        totalExpense

                    ],

                    backgroundColor: [

                        "#22c55e",

                        "#ef4444"

                    ],

                    borderWidth: 0

                }

            ]

        },

        options: {

            responsive: true,

            maintainAspectRatio: false,

            cutout: "65%",

            plugins: {

                legend: {

                    position: "bottom",

                    labels: {

                        color: "white",

                        padding: 20,

                        font: {

                            size: 14,

                            weight: "bold"

                        }

                    }

                }

            }

        }

    });

}

// ================= SEARCH =================

const searchInput = document.getElementById("searchInput");

if(searchInput){

    searchInput.addEventListener("keyup", function(){

        const filter = this.value.toLowerCase();

        const rows = document.querySelectorAll("#transactionTable tbody tr");

        rows.forEach(row=>{

            const text = row.innerText.toLowerCase();

            row.style.display = text.includes(filter) ? "" : "none";

        });

    });

}console.log("SCRIPT LOADED");
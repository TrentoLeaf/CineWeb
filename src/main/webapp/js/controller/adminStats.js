(function () {
    'use strict';

    /* modulo statistiche incassi e film admin */
    angular.module('adminStats', ['statsModule'])
        .controller('AdminStatsController', ['$location', 'Films', 'Stats', function ($location, Films, Stats) {

            var ctrl = this;
            this.error = "";

            // oggetto con i dati per la generazione del grafico migliori clienti
            var topClientsData = {
                labels: [],
                datasets: [
                    {
                        fillColor: "rgba(151,187,205,0.5)",
                        strokeColor: "rgba(151,187,205,0.8)",
                        highlightFill: "rgba(151,187,205,0.75)",
                        highlightStroke: "rgba(151,187,205,1)",
                        data: []
                    }
                ]
            };

            // recupera i dati sui clienti migliori
            this.topUsers = function () {
                Stats.topUsers()
                    .success(function (data) {

                        ctrl.error="";

                        // inserimento i dati nell'oggetto per la generazione del grafico
                        for (var user in data) {
                            if (data[user].spent != undefined) {
                                topClientsData.labels.push(data[user].firstName + ' ' + data[user].secondName);
                                topClientsData.datasets[0].data.push(data[user].spent);
                            }
                        }

                        // crea grafico
                        var ctx = $("#topClients").get(0).getContext("2d");
                        var topClients = new Chart(ctx).Bar(topClientsData, {pointDot: false});
                    })
                    .error(function (error) {
                        ctrl.error = "Il caricamento dei dati è fallito.";
                    });
            };

            // array di dati per la generazione del grafico
            var filmsIncomeData = [];

            // recupera i dati sui film migliori
            this.filmsIncome = function () {
                Stats.grossingFilms()
                    .success(function (data) {
                        var colors = [
                            "#E52323",
                            "#1C82AF",
                            "#E8C800",
                            "#464646",
                            "#1BB21B"
                        ];
                        ctrl.error="";

                        // inserimento i dati nell'array per la generazione del grafico
                        for (var film in data) {
                            if (data[film].grossing != undefined) {
                                filmsIncomeData.push(
                                    {
                                        label: data[film].title,
                                        value: data[film].grossing,
                                        color: colors[data.indexOf(data[film]) % colors.length]
                                    }
                                );
                            }
                        }

                        // crea grafico
                        var ctx = $("#filmsIncome").get(0).getContext("2d");
                        var topFilms = new Chart(ctx).Doughnut(filmsIncomeData, {
                            animateScale: true
                        });
                    })
                    .error(function (error) {
                        ctrl.error = 'Errore in parse dati film income';
                    });
            };

            this.filmsIncome();
            this.topUsers();
        }]);
})();
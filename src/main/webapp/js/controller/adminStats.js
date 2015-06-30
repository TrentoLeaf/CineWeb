/*Created by Willo on 24/06/2015.*/
(function () {
    'use strict';

    angular.module('adminStats', ['statsModule'])
        .controller('AdminStatsController', ['$location', 'Films', 'Stats', function ($location, Films, Stats) {

            var ctrl = this;
            this.error = "";

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

            this.topUsers = function () {
                Stats.topUsers()
                    .success(function (data) {

                        ctrl.error="";

                        for (var user in data) {
                            if (data[user].spent != undefined) {
                                topClientsData.labels.push(data[user].firstName + ' ' + data[user].secondName);
                                topClientsData.datasets[0].data.push(data[user].spent);
                            }
                        }

                        var ctx = $("#topClients").get(0).getContext("2d");
                        var topClients = new Chart(ctx).Bar(topClientsData, {pointDot: false});
                    })
                    .error(function (error) {
                        ctrl.error = "Il caricamento dei dati è fallito.";
                    });
            };

            var filmsIncomeData = [];

            this.filmsIncome = function () {
                Stats.grossingFilms()
                    .success(function (data) {
                        var colors = [
                            "#464646",
                            "#E52323",
                            "#1BB21B",
                            "#1C82AF",
                            "#E8C800"
                        ];
                        ctrl.error="";

                        for (var film in data) {
                            if (data[film].grossing != undefined) {
                                filmsIncomeData.push(
                                    {
                                        label: data[film].title,
                                        value: data[film].grossing,
                                        color: colors[Math.floor(Math.random() * (colors.length - 0) + 0)]
                                    }
                                );

                                console.log((Math.random() * (colors.length - 0) + 0));
                                console.log(filmsIncomeData[film].color);
                            }
                        }

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
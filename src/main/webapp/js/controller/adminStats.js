/**
 * Created by Willo on 24/06/2015.
 */
(function () {
    'use strict';

    /*Io devo fare:
     * -Lista dei posti venduti per ciascuna programmazione
     * -Lista incassi per film
     * -Lista dei clienti che comprano di più
     * */

    angular.module('adminStats', ['statsModule'])
        .controller('AdminStatsController', ['$location', 'Films', 'Stats', function ($location, Films, Stats) {

            var ctrl = this;
            this.error = "";

            var topClientsData = {
                labels: [],
                datasets: [
                    {
                        label: "My dataset",
                        fillColor: "rgba(242,54,255,0.2)",
                        strokeColor: "rgba(242,54,255,1)",
                        pointColor: "rgba(242,54,255,1)",
                        pointStrokeColor: "#fff",
                        pointHighlightFill: "#fff",
                        pointHighlightStroke: "rgba(242,54,255,1)",
                        data: []
                    }
                ]
            };

            this.topUsers = function () {
                Stats.topUsers()
                    .success(function (data) {
                        console.log('Sto parsando i dati di top user');
                        /*Formato dati ricevuti
                         "uid": 3,
                         "firstName": "aaa",
                         "secondName": "aaa",
                         "tickets": 5,
                         "spent": 35
                         */
                        for(var user = 0; user <= data.size ; user++){
                            topClientsData.labels.push(data[user].firstName + ' ' + data[user].secondName);
                            console.log("name "+data[user].firstName + ' ' + data[user].secondName);
                            topClientsData.datasets.data.push(data[user].spent);
                            console.log("spent "+data[user].spent);
                        }
                        console.log('Fine parsing dati user');
                    })
                    .error(function (error) {
                        console.log('Errore in parse dati top users');
                    });
            };

            this.topUsers();

            var ctx = document.getElementById("topClients").getContext("2d");
            var topClients = new Chart(ctx).Bar(topClientsData, {pointDot: false});

            var filmsIncomeData = {
                labels: [],
                datasets: [
                    {
                        label: "My dataset",
                        fillColor: "rgba(151,187,205,0.2)",
                        strokeColor: "rgba(151,187,205,1)",
                        pointColor: "rgba(151,187,205,1)",
                        pointStrokeColor: "#fff",
                        pointHighlightFill: "#fff",
                        pointHighlightStroke: "rgba(151,187,205,1)",
                        data: []
                    }
                ]
            };

            this.filmsIncomeData = function () {
                Stats.grossingFilms()
                    .success(function (data) {
                        console.log('Sto parsando i dati di film income');
                        /*Formato dati ricevuti
                         "fid": 1,
                         "title": "Teo",
                         "grossing": 35
                         */
                        for(var film = 0; film <= data.size; film++){
                            filmsIncomeData.labels.push(data[film].title);
                            console.log("title "+data[film].title);
                            filmsIncomeData.datasets.data.push(data[film].grossing);
                            console.log("grossing "+data[film].grossing);
                        }
                        console.log('Fine parsing dati film');
                    })
                    .error(function (error) {
                        console.log('Errore in parse dati film income');
                    });
            };

            this.filmsIncomeData();

            var ctx = document.getElementById("filmsIncome").getContext("2d");
            var filmsIncome = new Chart(ctx).Doughnut(filmsIncomeData, {pointDot: true});
        }]);
})();
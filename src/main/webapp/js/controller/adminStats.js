/*Created by Willo on 24/06/2015.*/
(function () {
    'use strict';

    /*Io devo fare:
     * -Lista dei posti venduti per ciascuna programmazione
     * -Lista incassi per film
     * -Lista dei clienti che comprano di più
     * */


    var dataFarlocchi1 = [
        {
            firstName: 'asdf',
            secondName: 'jklò',
            spent: 12
        },{
            firstName: 'asdwf',
            secondName: 'jklò',
            spent: 12
        },{
            firstName: 'asdfq',
            secondName: 'jklò',
            spent: 12
        },{
            firstName: 'wasdf',
            secondName: 'jklò',
            spent: 12
        }
    ];

    var dataFarlocchi2 = [
        {
            title: 'fuck',
            grossing: 23
        },{
            title: 'fuck',
            grossing: 23
        },{
            title: 'fuck',
            grossing: 23
        },{
            title: 'fuck',
            grossing: 23
        }
    ];

    angular.module('adminStats', ['statsModule'])
        .controller('AdminStatsController', ['$location', 'Films', 'Stats', function ($location, Films, Stats) {

            var ctrl = this;
            this.error = "";
            var fillColors=[
                'rgba(242,54,255,0.2)',
                'rgba(151,187,205,0.2)'
            ];

            var topClientsData = {
                labels: [],
                datasets: []
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
                        for(var user in dataFarlocchi1){
                            topClientsData.labels.push(dataFarlocchi1[user].firstName + ' ' + dataFarlocchi1[user].secondName);
                            console.log("name "+dataFarlocchi1[user].firstName + ' ' + dataFarlocchi1[user].secondName);

                            topClientsData.datasets.push(
                                {
                                    fillColor: fillColors[user % fillColors.size],
                                    data : dataFarlocchi1[user].spent
                                }
                            );

                            console.log("spent "+dataFarlocchi1[user].spent);
                        }
                        console.log('Fine parsing dati user, label: ' + topClientsData.labels);
                        console.log('dati: ' + topClientsData.datasets.data);
                    })
                    .error(function (error) {
                        console.log('Errore in dati top users');
                    });
            };

            this.topUsers();

            var ctx = document.getElementById("topClients").getContext("2d");
            var topClients = new Chart(ctx).Bar(topClientsData, {pointDot: false});


            /*Questo lo faccio appena capisco come popolare sto coso sopra*/
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
                        for(var film = 0; film <= dataFarlocchi2.size; film+=1){
                            filmsIncomeData.labels.push(dataFarlocchi2[film].title);
                            console.log("title "+dataFarlocchi2[film].title);
                            filmsIncomeData.datasets.data.push(dataFarlocchi2[film].grossing);
                            console.log("grossing "+dataFarlocchi2[film].grossing);
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
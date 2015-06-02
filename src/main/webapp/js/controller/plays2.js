// ANDR REMOVE


(function () {
    "use strict";

    angular.module('PlaysModule', ['filmsPlaysModule'])
        .controller('PlaysController', ['$location', 'CompletePlays', function ($location, CompletePlays) {
            this.current = {};
            this.show_trailer_for_current = "Guarda il Trailer";

            var ctrl = this;

            this.archive = [
                {
                    date: 'Data 1',
                    films: [
                        {
                            title: "Titolo 1",
                            date: "Data 1",
                            time: ["17:00", "21:00"],
                            gender: "Genere 1",
                            playbill: "img/temporary/img1.jpg",
                            description: "Descrizione 1"
                        }, {
                            title: "Titolo 2",
                            date: "Data 1",
                            time: ["17:00", "21:00"],
                            gender: "Genere 2",
                            playbill: "img/temporary/img2.jpg",
                            description: "Descrizione 2"
                        }, {
                            title: "Titolo 3",
                            date: "Data 1",
                            time: ["16:00"],
                            gender: "Genere 3",
                            playbill: "img/temporary/img3.jpg",
                            description: "Descrizione 3"
                        }, {
                            title: "Titolo 4",
                            date: "Data 1",
                            time: ["17:00", "21:00"],
                            gender: "Genere 4",
                            playbill: "img/temporary/img4.jpg",
                            description: "Descrizione 4"
                        }, {
                            title: "Titolo 5",
                            date: "Data 1",
                            time: ["17:00", "21:00"],
                            gender: "Genere 5",
                            playbill: "img/temporary/img5.jpg",
                            description: "Descrizione 5"
                        }]
                }];

            this.loadData = function () {
                CompletePlays.playsByDate().then(
                    function (data) {
                        ctrl.archive = data;
                    },
                    function (error) {
                        // TODO: handle error

                    }
                );
            };

            this.isNow = function(date){
                var act = new Date();
                console.log("This is the date: "+date+" and this is the actual: "+act);
                return true;
            };

            this.setCurrent = function (date, film) {
                this.current = this.archive[date].films[film];
                this.current['date'] = date;
                $('#modal').openModal();
            };

            this.closeWindow =  function () {
                $('#modal').closeModal();
            }

            this.showTrailer = function () {
                if (this.show_trailer_for_current == "Guarda il Trailer") {
                    this.show_trailer_for_current = "Guarda la Locandina";
                } else {
                    this.show_trailer_for_current = "Guarda il Trailer";
                }
            }

            this.log = function() {
                console.log("pllog");
            }



        }]);
})();
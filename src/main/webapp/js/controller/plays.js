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
                            title: "Titolo 1sacsdfd ds ssdff dsfdsfsfd",
                            date: "Data 1",
                            time: ["17:00", "21:00"],
                            gender: "Genere 1",
                            playbill: "img/temporary/mad-max-fury-road-locandina-400x250.jpg",
                            description: "Descrizione 1"
                        },
                        {
                            title: "Titolo 2",
                            date: "Data 1",
                            time: ["17:00", "21:00"],
                            gender: "Genere 2",
                            playbill: "img/temporary/terminator_genisys_ver9-201x298.jpg",
                            description: "Descrizione 2"
                        },
                        {
                            title: "Titolo 3",
                            date: "Data 1",
                            time: ["16:00"],
                            gender: "Genere 3",
                            playbill: "img/temporary/the-lazarus-effect-LOCANDINA-400x250.jpg",
                            description: "Descrizione 3"
                        },
                        {
                            title: "Titolo 4",
                            date: "Data 1",
                            time: ["17:00", "21:00"],
                            gender: "Genere 4",
                            playbill: "img/temporary/san-andreas-locandina-400x250.jpg",
                            description: "Descrizione 4"
                        },
                        {
                            title: "Titolo 5",
                            date: "Data 1",
                            time: ["17:00", "21:00"],
                            gender: "Genere 5",
                            playbill: "img/temporary/tomorrowland-il-mondo-di-domani-locandina-400x250.jpg",
                            description: "Descrizione 5"
                        }
                    ]
                }
            ];

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

            this.isNow = function (date) {
                var act = new Date();
                console.log("This is the date: " + date + " and this is the actual: " + act);
                return true;
            };

            this.setCurrent = function (date, film) {
                this.current = this.archive[date].films[film];
                this.current['date'] = date;
                $('#modal').openModal();
            };

            this.closeWindow = function () {
                console.log("Close window...");
                $('#modal').closeModal();
                // TODO call setTab(1)
            };

            this.showTrailer = function () {
                if (this.show_trailer_for_current == "Guarda il Trailer") {
                    this.show_trailer_for_current = "Guarda la Locandina";
                } else {
                    this.show_trailer_for_current = "Guarda il Trailer";
                }
            };

            this.log = function () {
                console.log("pllog");
            };

        }]);
})();
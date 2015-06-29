(function () {
    "use strict";

    angular.module('PlaysModule', ['filmsPlaysModule'])
        .controller('PlaysController', ['$rootScope', '$location', 'CompletePlays','$sce', function ($rootScope, $location, CompletePlays, $sce) {
            this.current = {};
            this.show_trailer_for_current = "Guarda il Trailer";

            var ctrl = this;


            this.archive = [ // TODO non serve più
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
                            time: ["17:00", "21:00", "22:00", "23:00", "24:00"],
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


            this.isNow = function (date) {
                var act = new Date();
                console.log("This is the date: " + date + " and this is the actual: " + act);
                return true;
            };

            this.setCurrent = function (date, film) {
                this.current = $rootScope.playsByDate[date].films[film];
                this.current['date'] = date;
                $('#modal').openModal();
            };

            this.trustSrc = function(src) {
                return $sce.trustAsResourceUrl(src);
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
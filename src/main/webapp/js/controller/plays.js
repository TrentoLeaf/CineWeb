(function() {
    "use strict";



    angular.module('PlaysModule', []).controller('PlaysController', function () {
        this.current = {};

        this.cart = [];

        this.archive = [
            {
                date: 'Data 1',
                films: [
                    {
                        title: "Titolo 1",
                        time: "Orario 1",
                        gender: "Genere 1",
                        playbill: "img/temporary/img1.jpg",
                        description: "Descrizione 1"
                    }, {
                        title: "Titolo 2",
                        time: "Orario 2",
                        gender: "Genere 2",
                        playbill: "img/temporary/img2.jpg",
                        description: "Descrizione 2"
                    }, {
                        title: "Titolo 3",
                        time: "Orario 3",
                        gender: "Genere 3",
                        playbill: "img/temporary/img3.jpg",
                        description: "Descrizione 3"
                    }, {
                        title: "Titolo 4",
                        time: "Orario 4",
                        gender: "Genere 4",
                        playbill: "img/temporary/img4.jpg",
                        description: "Descrizione 4"
                    }, {
                        title: "Titolo 5",
                        time: "Orario 5",
                        gender: "Genere 5",
                        playbill: "img/temporary/img5.jpg",
                        description: "Descrizione 5"
                    }
                ]
            }, {
                date: 'Data 2',
                films: [
                    {
                        title: "Titolo 6",
                        time: "Orario 6",
                        gender: "Genere 6",
                        playbill: "img/temporary/img6.jpg",
                        description: "Descrizione 6",
                    }, {
                        title: "Titolo 7",
                        time: "Orario 7",
                        gender: "Genere 7",
                        playbill: "img/temporary/img7.jpg",
                        description: "Descrizione 7",
                    }, {
                        title: "Titolo 8",
                        time: "Orario 8",
                        gender: "Genere 8",
                        playbill: "img/temporary/img8.jpg",
                        description: "Descrizione 8"
                    }, {
                        title: "Titolo 9",
                        time: "Orario 9",
                        gender: "Genere 9",
                        playbill: "img/temporary/img9.jpg",
                        description: "Descrizione 9"
                    }
                ]
            }
        ];

        this.setCurrent = function(date, film){
            this.current = this.archive[date].films[film];
        };

        this.addToCart = function(currentFilm){
            this.cart.push(currentFilm);
        };

    });
})();
(function () {
    "use strict";

    angular.module('CartModule', []).controller('CartController', ['$rootScope', function ($rootScope) {

        this.prices = {
            ticket_normale: 8,
            ticket_ridotto: 5,
            ticket_militare: 6,
            ticket_disabile: 7
        };


        // carrello che contiene oggetti film modificati
        $rootScope.cart = [{
            title: "Titolo 1",
            date: "Data 1",
            time: "Orario 1",
            gender: "Genere 1",
            playbill: "img/temporary/img1.jpg",
            description: "Descrizione 1",
            normale: 2,
            ridotto: 0,
            militari: 0,
            disabili: 0
        }];

        this.total = 0.00;

        this.addToCart = function (film) {
            // numero di ticket per tipologia
            film['normale'] = 1;
            film['ridotto'] = 0;
            film['militari'] = 0;
            film['disabili'] = 0;
            $rootScope.cart.push(film);
            this.updateTotal();
            console.log($rootScope.cart);
        };

        this.removeFromCart = function (film) {
            if (i > -1) {
                this.cart.splice(film);
                this.updateTotal();
            }
        };

        this.updateTiket = function (film, type, number) {
            film[type] = number;
            this.updateTotal();
        };

        this.updateTotal = function () {
            this.total = 0;

            for (var i = 0; i < $rootScope.cart.length; i++) {

                this.total = this.total
                    + ((this.prices['ticket_normale'] * $rootScope.cart[i]['normale'])
                    + (this.prices['ticket_ridotto'] * $rootScope.cart[i]['ridotto'])
                    + (this.prices['ticket_militare'] * $rootScope.cart[i]['militari'])
                    + (this.prices['ticket_disabile'] * $rootScope.cart[i]['disabili']));
            }
        };

    }]);
})();
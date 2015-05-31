(function () {
    "use strict";

    angular.module('CartModule', []).controller('CartController', ['$rootScope', function ($rootScope) {

        this.prices = {
            ticket_normale: 8.50,
            ticket_ridotto: 5.50,
            ticket_militare: 6,
            ticket_disabile: 7
        };

        $rootScope.ticket_types = [{name: "Normale"},{name:'Ridotto'},{name:'Militari'},{name:'Disabili'}];

        this.maxTickets = 10;

        // carrello che contiene oggetti film modificati
        $rootScope.cart = [{
            title: "Titolo 1",
            date: "Data 1",
            time: "Orario 1",
            gender: "Genere 1",
            playbill: "img/temporary/img1.jpg",
            description: "Descrizione 1",
            dropdowns: [1, 1],
            normale: 2,
            ridotto: 0,
            militari: 0,
            disabili: 0
        }];

        $rootScope.total = 0.00;

        this.addToCart = function (film) {
            // numero di ticket per tipologia
            film['normale'] = 1;
            film['ridotto'] = 0;
            film['militari'] = 0;
            film['disabili'] = 0;
            film['dropdowns'] = [1];
            $rootScope.cart.push(film);
            this.updateTotal();
        };

        this.removeFromCart = function (film) {
            $rootScope.cart.splice(film);
            this.updateTotal();
        };

        this.updateTiket = function (film, type, number) {
            if (number == 0) {
                this.removeDropDown(film);
            } else {
                $rootScope.cart[film][type] = number;
            }
            this.updateTotal();
        };

        this.addDropDown = function (film) {
            if (($rootScope.cart[film]['dropdowns']).length < 4) {
                $rootScope.cart[film]['dropdowns'].push(1);
            }
            console.log($rootScope.cart[film]);
        };

        this.removeDropDown = function (film) {
            if ($rootScope.cart[film]['dropdowns'].length == 1) {
                this.removeFromCart(film);
            }
            else {
                $rootScope.cart[film]['dropdowns'].push(1);
            }
        };

        this.updateTotal = function () {
            $rootScope.total = 0;

            for (var i = 0; i < $rootScope.cart.length; i++) {
                $rootScope.total = $rootScope.total
                    + ((this.prices['ticket_normale'] * $rootScope.cart[i]['normale'])
                    + (this.prices['ticket_ridotto'] * $rootScope.cart[i]['ridotto'])
                    + (this.prices['ticket_militare'] * $rootScope.cart[i]['militari'])
                    + (this.prices['ticket_disabile'] * $rootScope.cart[i]['disabili']));
            }
        };

    }]);
})();
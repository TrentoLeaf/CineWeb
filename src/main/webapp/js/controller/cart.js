(function () {
    "use strict";

    angular.module('cartModule', [])
        .controller('CartController', ['$rootScope', function ($rootScope) {

            // TODO dati da recuperare dal server
            $rootScope.tickets = [
                {type: "normale", name: "Normale", price: 8.50},
                {type: "ridotto", name: "Ridotto", price: 5.50},
                {type: "militare", name: "Militare", price: 6},
                {type: "disabile", name: "Disabile", price: 7}
            ];

            this.maxTickets = 10;

            // carrello che contiene oggetti film modificati
            $rootScope.cart = $rootScope.cart || [];
            $rootScope.total = $rootScope.total || 0.00;

            this.addToCart = function (film, time_index) {

                // film: l'oggetto film da acquistare
                // time_index: l'indice dell'ora (nell' array time di film) da prenotare

                var duplicate = false;

                // TODO controllo di non inserire dublicati
                /* for (var i = 0; i< $rootScope.cart.length; i++) {
                 // TODO assicurarsi che film abbia un fid e che i time siano diversi
                 if (film.fid == $rootScope.cart[i].fid && (film.time[time_index] == $rootScope.cart[i].time)) {
                 duplicate = true;
                 i = $rootScope.cart.length;
                 }
                 console.log(duplicate);
                 }*/

                if (!duplicate) {
                    // copio film per non sporcare i film in programmazione con valori del carrello
                    var newFilm = this.cloneObject(film);
                    // aggiungo un dropdown con tipo e numero di biglietti
                    newFilm['tickets'] = [];
                    newFilm['tickets'].push({type: $rootScope.tickets[0].type, number: 1});
                    newFilm['time'] = film['time'][time_index];
                    $rootScope.cart.push(newFilm);
                    this.updateTotal();
                }
            };

            this.removeFromCart = function (film) {
                $rootScope.cart.splice(film, 1); // rimuove 1 elemento a partire da 'film'
                this.updateTotal();
            };

            // aggiunge un nuovo selettore per tipo e numero di biglietti
            this.addDropDown = function (film) {
                if (($rootScope.cart[film].tickets.length) < 4) {
                    $rootScope.cart[film].tickets.push({type: $rootScope.tickets[0].type, number: 1});
                }
                this.updateTotal();
            };

            /*
             * aggiorna il totale controllando per tutti il film nel carrello,
             * il numero e tipo di biglietti e li moltiplica per il  loro prezzo
             */
            this.updateTotal = function () {
                $rootScope.total = 0;

                for (var i = 0; i < $rootScope.cart.length; i++) {
                    for (var j = 0; j < $rootScope.tickets.length; j++) {
                        var num = 0;
                        for (var k = 0; k < $rootScope.cart[i].tickets.length; k++) {
                            if ($rootScope.cart[i].tickets[k].type == $rootScope.tickets[j].type) {
                                num = num + $rootScope.cart[i].tickets[k].number;
                            }
                        }
                        $rootScope.total = $rootScope.total + ($rootScope.tickets[j]['price'] * num);
                    }
                }

            };

            this.cloneObject = function (obj) {
                return (JSON.parse(JSON.stringify(obj)));
            };

        }]);
})();
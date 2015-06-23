(function () {
    "use strict";

    String.prototype.capitalizeFirstLetter = function () {
        return this.charAt(0).toUpperCase() + this.slice(1);
    };

    angular.module('cartModule', ['pricesModule', 'storageModule'])
        .controller('CartController', ['$rootScope', 'Prices', '$location', 'StorageService', function ($rootScope, Prices, $location, StorageService) {

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
                    // abilita il pulsante 'prosegui'
                    $('#btn-go-to-buy').removeClass('hide');
                   // $rootScope.updateTotal();
                }
            };

            this.removeFromCart = function (film) {
                $rootScope.cart.splice(film, 1); // rimuove 1 elemento a partire da 'film'
                // se il carrello è vuoto, tolgo il pulsante 'prosegui'
                if ($rootScope.cart.length == 0) {
                    $('#btn-go-to-buy').addClass('hide');
                }
              //  $rootScope.updateTotal();
            };

            // aggiunge un nuovo selettore per tipo e numero di biglietti
            this.addDropDown = function (film) {
                if (($rootScope.cart[film].tickets.length) < 4) {
                    $rootScope.cart[film].tickets.push({type: $rootScope.tickets[0].type, number: 1});
                }
             //   $rootScope.updateTotal();
            };


            this.proceed = function () {
                $location.path("/buy");
            };

            this.cloneObject = function (obj) {
                return (JSON.parse(JSON.stringify(obj)));
            };






        }]);
})();
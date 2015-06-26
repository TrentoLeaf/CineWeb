(function () {
    "use strict";

    String.prototype.capitalizeFirstLetter = function () {
        return this.charAt(0).toUpperCase() + this.slice(1);
    };

    angular.module('cartModule', ['pricesModule'])
        .controller('CartController', ['$rootScope', 'Prices', '$location', 'Auth', function ($rootScope, Prices, $location, Auth) {

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
                    // anima l'icona del carrello dopo un acquisto carrello
                    animateCartIcon();
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

                // TODO verificare che il server dia l'ok  e che tutti i valori e spettacoli siano giusti

                $('.cart-loader').addClass('active');

                // TODO semplificare (auth.me si può sostituire con $rootscope.isUserLogged)
                // check if the user is already logged
                Auth.me().then(
                    function (data) {
                        $('.cart-loader').removeClass('active');

                        // reset di afterLogin
                        $rootScope.afterLogin = "normal";
                        // vai alla pagina di acquisto
                        $location.path("/buy");

                    },
                    function () { /* not logged */
                        $('.cart-loader').removeClass('active');

                        // setta afterLogin a buy (per ritornare alla procedura d'acquisto)
                        $rootScope.afterLogin = "buy";
                        // vai alla pagina di login
                        $location.path('/login');
                    }
                );

            };

            this.cloneObject = function (obj) {
                return (JSON.parse(JSON.stringify(obj)));
            };

            // anima l'icona del carrello
            function animateCartIcon () {

                // set timeout to remove the animation
                setTimeout (function () {
                    $('.cart-icon').css({
                        '-webkit-animation': '',
                        '-moz-animation':    '',
                        '-o-animation':      '',
                        'animation':         ''
                    });
                }, 1650); // 0.3s * 5.5 = 1.65

                // start the keyframe cart-animation
                $('.cart-icon').css({
                    '-webkit-animation': 'cart-animation 0.3s 5.5',
                    '-moz-animation':    'cart-animation 0.3s 5.5',
                    '-o-animation':      'cart-animation 0.3s 5.5',
                    'animation':         'cart-animation 0.3s 5.5'
                });
            }


        }]);
})();
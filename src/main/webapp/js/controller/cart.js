(function () {
    "use strict";

    /* modulo per la gestione del carrello */
    angular.module('cartModule', ['pricesModule'])
        .controller('CartController', ['$rootScope', 'Prices', '$location', function ($rootScope, Prices, $location) {

            // aggiunge una proiezione al carrello
            this.addToCart = function (film, play_index) {

                /* film: l'oggetto film da acquistare
                 * play_index: l'indice (per array 'plays' in film) dello spettacolo da prenotare
                 */
                var duplicate = false;

                // controllo di non inserire un duplicato verificando l'id della proiezione (pid)
                for (var i = 0; i< $rootScope.cart.length; i++) {
                    if (film.plays[play_index].pid == $rootScope.cart[i].pid) {
                        // proiezione già presente nel carrello
                        duplicate = true;
                        i = $rootScope.cart.length;
                    }
                }

                if (!duplicate) {
                    // copio film per non sporcare l'array dei film in programmazione con valori del carrello
                    var newFilm = $rootScope.cloneObject(film);
                    // aggiungo un dropdown con tipo e numero di biglietti
                    newFilm.tickets = [];
                    newFilm.tickets.push({type: $rootScope.tickets[0].type, number: 1});
                    newFilm.time = film.plays[play_index].time;
                    newFilm.pid = film.plays[play_index].pid;
                    newFilm.rid = film.plays[play_index].rid;
                    newFilm.free = film.plays[play_index].free;
                    // pulisco l'oggetto
                    delete newFilm.date;
                    delete newFilm.plays;
                    delete newFilm.genre;
                    delete newFilm.trailer;
                    delete newFilm.plot;
                    delete newFilm.duration;

                    $rootScope.cart.push(newFilm);
                    // anima l'icona del carrello dopo un acquisto carrello
                    animateCartIcon();
                }
            };

            // rimuove una proiezione dal carrello
            this.removeFromCart = function (film) {
                // film: indice per l'array $rootScope.cart
                $rootScope.cart.splice(film, 1); // rimuove 1 elemento a partire da 'film'
            };

            // aggiunge un nuovo selettore dropdown per tipo e numero di biglietti
            this.addDropDown = function (film) {
                // se ho più selettori che tipologie di biglietto, un nuovo selettore è inutile
                if (($rootScope.cart[film].tickets.length) < $rootScope.tickets.length) {
                    $rootScope.cart[film].tickets.push({type: $rootScope.tickets[0].type, number: 1});
                }
            };

            this.removeDropDown = function (film_index, dropdown_index) {
                console.log("remov");
                $rootScope.cart[film_index].tickets.splice(dropdown_index, 1);
                console.log($rootScope.cart);
            };

            // redirect alla pagina di selezione posti
            this.proceed = function () {

                /* animazione 'loading' pulsante
                 $('.cart-loader').addClass('active');
                 $('.cart-loader').removeClass('active');
                 */
                // vai alla pagina di acquisto
                $location.path("/buy");

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
                }, 1650); // 0.3s * 5.5 (num rotazioni) = 1.65

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
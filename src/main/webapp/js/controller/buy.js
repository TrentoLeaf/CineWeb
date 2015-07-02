(function () {
    'use strict';

    angular.module('buyModule', ['usersModule', 'storageModule', 'constantsModule'])
        .controller('BuySeatController', ['$rootScope', '$location', '$anchorScroll', '$window', 'BuyProcedure', 'Rooms', function ($rootScope, $location, $anchorScroll, $window, BuyProcedure, Rooms) {

            var ctrl = this;

            this.go = false;
            this.error = "";

            // errori
            var OK = 0;
            var ERROR = 1;
            var NOTHING = 2;

            function setError(error) {
                switch (error) {
                    case OK :  ctrl.error = ""; break;
                    case ERROR : ctrl.error = "Sembra che tu sia cercando di acquistare proiezioni o biglietti non più disponibili. Per favore, ricontrolla il carrello."; break;
                    case NOTHING : ctrl.error = ""; break;
                }
            }

            /* film di cui si stanno attualmente selezionando i posti, contiene id, locandina, titolo, data-ora, num_posti */
            $rootScope.buy.shared_obj.selected_seats = [];
            /* array di oggetti. Gli oggetti sono i posti selezionati */
            $rootScope.buy.shared_obj.film = {};

            /* chiamata dal carrello quando l'utente vuole procedere con l'acquisto (pulsante procedi del carrello)*/
            this.start_buy = function () {
                $rootScope.buy.shared_obj.film = {};
                $rootScope.buy.shared_obj.selected_seats = [];
                $rootScope.buy.data_to_server = [];
                $rootScope.buy.data_from_server_index = -1;


                $('.buy-seats-loader').addClass('active');
                setError(NOTHING);
                ctrl.go=false;

                // check if the user is already logged
                if ($rootScope.isUserLogged) {

                    // reset di afterLogin
                    $rootScope.afterLogin = "normal";

                    //verificare che il server dia l'ok  e che tutti i valori e spettacoli siano giusti
                    BuyProcedure.proceed($rootScope.cart, $rootScope.user)
                        .success(function () {  // tutto ok
                            $('.buy-seats-loader').removeClass('active');
                            setError(OK);
                            $rootScope.cart = [];
                            $rootScope.buy.data_from_server = $rootScope.cart;
                            // procedi
                            ctrl.go=true;
                            ctrl.next_buy();
                        })
                        .error(function (data) {    // biglietti o spettacoli non più disponibili
                            // ricarico il carrello
                            $rootScope.cart = data;
                            $('.buy-seats-loader').removeClass('active');
                            ctrl.go= false;
                            setError(ERROR);

                        });

                } else { /* not logged */
                    $('.buy-seats-loader').removeClass('active');
                    // setta afterLogin a buy (per ritornare alla procedura d'acquisto)
                    $rootScope.afterLogin = "buy";
                    // vai alla pagina di login
                    $location.path('/login');
                }



                this.next_buy(); //tmp

            };

            /* per ogni spettacolo acquistato presenta la pagina di scelta dei posti. Rimanda poi alla pagina di riepilogo*/
            this.next_buy = function () {

                $rootScope.buy.shared_obj.selected_seats = [];
                // next film in cart
                $rootScope.buy.data_from_server_index++;
                if ($rootScope.buy.data_from_server_index < $rootScope.buy.data_from_server.length) {

                    $rootScope.buy.shared_obj.film = $rootScope.buy.data_from_server[$rootScope.buy.data_from_server_index];

                    // conta il max di biglietti selezionabili
                    $rootScope.buy.shared_obj.film.seats_selected = 0;
                    for (var i = 0; i < $rootScope.buy.shared_obj.film.tickets.length; i++) {
                        $rootScope.buy.shared_obj.film.seats_selected = $rootScope.buy.shared_obj.film.seats_selected + $rootScope.buy.shared_obj.film.tickets[i].number;
                    }

                    // richiedi la sala
                    Rooms.getRoomStatus($rootScope.buy.shared_obj.film.rid)
                        .success(function (data) {
                            $rootScope.buy.shared_obj.film.seats = data.seats;
                        })
                        .error(function () {
                            // TODO maledetto internet
                        });

                    $('.buy-loader').removeClass('active');
                    // back to top of page
                    $location.hash('main-content');
                    $anchorScroll();
                }
                else { // scelta dei posti terminata
                    $('.buy-loader').removeClass('active');
                    // back to top of page
                    $location.hash('main-content');
                    $anchorScroll();
                    // Redirect alla pagina di riepilogo
                    $location.path('/buy_last_step');
                }


            };

            /* chiamata da bottone avanti nella scelta posti */
            this.save_seats = function () {
                $('.buy-loader').addClass('active');
                // creo un nuovo oggetto
                this.ff = this.cloneObject($rootScope.buy.shared_obj.film);
                // aggiungo allo spettacolo i posti selezionati
                this.ff.selected_seats = $rootScope.buy.shared_obj.selected_seats;
                delete this.ff.seats;
                delete this.ff.seats_selected;
                // salvo l'oggetto nell'array che inverò al server una volta completate le scelte dei posti di tutti gli spettacoli
                $rootScope.buy.data_to_server.cart.push(this.ff);
                this.next_buy();
            };


            this.open_modal = function () {
                $('#modal_buy_cancel').openModal();
            };

            this.close_modal = function() {
                $('#modal_buy_cancel').closeModal();
            };

            this.cancel_procedure = function () {
                $rootScope.buy.shared_obj.film = {};
                $rootScope.buy.shared_obj.selected_seats = [];
                $rootScope.buy.data_to_server.cart = [];
                $rootScope.buy.data_from_server = [];
                $rootScope.buy.data_from_server_index = -1;

                console.log("buy procedure canceled!");
                this.close_modal();
                $location.path('/today');
            };

            this.cloneObject = function (obj) {
                return (JSON.parse(JSON.stringify(obj)));
            };


            this.start_buy();

        }])

        .controller('BuySummaryController', ['$rootScope', '$location', 'BuyProcedure', function ($rootScope, $location, BuyProcedure) {

            var ctrl = this;
            this.cd = {
            };
            // messaggi
            this.error_msg = "";
            this.ERROR_CARD = "I dati inseriti sembrano non essere validi. Controlla.";
            this.pay = function () {
                $('.buy-loader').addClass('active');
                // salvo i dati
                $rootScope.buy.data_to_server.userid = $rootScope.user.email;
                $rootScope.buy.data_to_server.creditCard = ctrl.cd;

                // paga
                BuyProcedure.pay($rootScope.buy.data_to_server)
                    .success(function () {
                        $('.buy-loader').removeClass('active');
                        $rootScope.buy.complete_error = false;
                        $rootScope.cart = [];
                        $rootScope.buy.data_to_server = {};
                        $location.path('/buy_complete');
                    })
                    .error(function (data, error) {
                        if (error == "1") { // TODO inventare error carta credito
                            ctrl.error_msg = ctrl.ERROR_CARD;
                            $('.buy-loader').removeClass('active');
                        } else if (error == "2") { // TODO inventare errore posti
                            $rootScope.cart = [];
                            $rootScope.cart = data;
                            $('.buy-loader').removeClass('active');
                            $rootScope.buy.complete_error = true;
                            $location.path('/buy_complete');
                        }
                    });


                $rootScope.buy.complete_error = false; // temp
                $location.path('/buy_complete'); // temp
            };

            this.open_modal = function () {
                $('#modal_buy_cancel').openModal();
            };

            this.close_modal = function() {
                $('#modal_buy_cancel').closeModal();
            };

            this.cancel_procedure = function () {
                $rootScope.buy.shared_obj.film = {};
                $rootScope.buy.shared_obj.selected_seats = [];
                $rootScope.buy.data_to_server = {};
                $rootScope.buy.data_from_server = [];
                $rootScope.buy.data_from_server_index = -1;

                console.log("buy procedure canceled!");
                this.close_modal();
                $location.path('/today');
            };

        }])

        .controller('BuyCompleteController', ['$rootScope', function($rootScope) {

            this.BUY_COMPLETE_SUCCESS = "Il pagamento è andato a buon fine. Grazie per aver acquistato sul nostro sito! A breve riceverai i biglietti direttamente nella tua casella di posta.";
            this.BUY_COMPLETE_ERROR = "Oh oh... Sembra che qualcuno abbia già prenotato i posti che hai selezionato o hai impiegato troppo a completare la procedura d'acquisto. Ti invitiamo a riprovare.";
            this.buy_complete_msg = "";

            this.setCompleteMsg = function (error) {
                $('.buy-complete-msg').removeClass('white-text red-text');

                if (!error) {
                    $('.buy-complete-msg').addClass('white-text');
                    this.buy_complete_msg = this.BUY_COMPLETE_SUCCESS;
                    console.log(this.buy_complete_msg);
                } else {
                    $('.buy-complete-msg').addClass('red-text');
                    this.buy_complete_msg = this.BUY_COMPLETE_ERROR;
                }
            };


            this.setCompleteMsg($rootScope.buy.complete_error);

        }]);






})();


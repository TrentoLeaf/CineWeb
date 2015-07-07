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
                    case OK :
                        ctrl.error = "";
                        break;
                    case ERROR :
                        ctrl.error = "Sembra che tu sia cercando di acquistare proiezioni o biglietti non più disponibili. Per favore, ricontrolla il carrello.";
                        break;
                    case NOTHING :
                        ctrl.error = "";
                        break;
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
                $rootScope.buy.data_to_server.cart = [];
                $rootScope.buy.data_from_server_index = -1;


                $('.buy-seats-loader').addClass('active');
                setError(NOTHING);
                ctrl.go = false;

                // check if the user is already logged
                if ($rootScope.isUserLogged) {

                    // reset di afterLogin
                    $rootScope.afterLogin = "normal";
                    //verificare che il server dia l'ok  e che tutti i valori e spettacoli siano giusti
                    BuyProcedure.proceed($rootScope.cart)
                        .success(function () {  // tutto ok
                            $('.buy-seats-loader').removeClass('active');
                            setError(OK);
                            //TODO capire perchè l'ho messo: $rootScope.cart = [];
                            $rootScope.buy.data_from_server = ctrl.cloneObject($rootScope.cart);
                            console.log("successo, data from server ");
                            console.log($rootScope.buy.data_from_server);
                            // procedi
                            ctrl.go = true;
                            ctrl.next_buy();
                        })
                        .error(function (data) {    // biglietti o spettacoli non più disponibili
                            // ricarico il carrello
                            $rootScope.cart = data;
                            $('.buy-seats-loader').removeClass('active');

                            console.log("errore, data from server ");
                            console.log($rootScope.buy.data_from_server);
                            ctrl.go = false;
                            setError(ERROR);

                            // redirect alla pagina di errore
                            $location.path("/buy_start_error");

                        });

                } else { /* not logged */
                    $('.buy-seats-loader').removeClass('active');
                    // setta afterLogin a buy (per ritornare alla procedura d'acquisto)
                    $rootScope.afterLogin = "buy";
                    // vai alla pagina di login
                    $location.path('/login');
                }

            };

            /* per ogni spettacolo acquistato presenta la pagina di scelta dei posti. Rimanda poi alla pagina di riepilogo*/
            this.next_buy = function () {

                $rootScope.buy.shared_obj.selected_seats = [];
                // next film in cart
                $rootScope.buy.data_from_server_index++;

                if ($rootScope.buy.data_from_server_index < $rootScope.buy.data_from_server.length) {

                    // reset msgs
                    ctrl.seats_msg = "";

                    $rootScope.buy.shared_obj.film = $rootScope.buy.data_from_server[$rootScope.buy.data_from_server_index];

                    // conta il max di biglietti selezionabili
                    $rootScope.buy.shared_obj.film.seats_selected = 0;
                    for (var i = 0; i < $rootScope.buy.shared_obj.film.tickets.length; i++) {
                        $rootScope.buy.shared_obj.film.seats_selected = $rootScope.buy.shared_obj.film.seats_selected + $rootScope.buy.shared_obj.film.tickets[i].number;
                    }

                    // richiedi la sala
                    Rooms.getRoomStatus($rootScope.buy.shared_obj.film.pid)
                        .success(function (data) {
                            $rootScope.buy.shared_obj.selected_seats = [];
                            $rootScope.buy.shared_obj.film.seats = data.seats;
                        })
                        .error(function () {
                            // TODO maledetto internet
                        });

                    $('.buy-loader').removeClass('active');
                    // back to top of page
                    $anchorScroll();
                }
                else { // scelta dei posti terminata
                    $('.buy-loader').removeClass('active');
                    // back to top of page
                    $anchorScroll();
                    // Redirect alla pagina di riepilogo
                    $location.path('/buy_last_step');
                }


            };

            /* chiamata da bottone avanti nella scelta posti */
            this.save_seats = function () {

                // check if all seats are selected
                if ($rootScope.buy.shared_obj.film.seats_selected > 0) {
                    ctrl.seats_msg = "Devi ancora selezionare i posti rimanenti";
                } else {
                    $('.buy-loader').addClass('active');
                    // creo un nuovo oggetto
                    ctrl.ff = ctrl.cloneObject($rootScope.buy.shared_obj.film);
                    // aggiungo allo spettacolo i posti selezionati
                    ctrl.ff.selected_seats = ctrl.cloneObject($rootScope.buy.shared_obj.selected_seats);
                    delete ctrl.ff.seats;
                    delete ctrl.ff.seats_selected;
                    // salvo l'oggetto nell'array che inverò al server una volta completate le scelte dei posti di tutti gli spettacoli
                    $rootScope.buy.data_to_server.cart.push(ctrl.ff);
                    this.next_buy();
                }

            };


            this.open_modal = function () {
                $('#modal_buy_cancel').openModal();
            };

            this.close_modal = function () {
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
                number: "",
                month: 1,
                year: 2015,
                cvv: "",
                name: ""
            };
            this.importToPay = 0;
            // messaggi
            this.error_msg = "";
            this.ERROR_CARD = "I dati inseriti sembrano non essere validi. Controlla.";

            this.pay = function () {
                $('.buy-loader').addClass('active');
                ctrl.error_msg = "";


                // parse selected_seats to int
                for (var i = 0; i < $rootScope.buy.data_to_server.cart.length; i++) {

                    for (var j = 0; j < $rootScope.buy.data_to_server.cart[i].selected_seats.length; j++) {
                        var string_oby = $rootScope.buy.data_to_server.cart[i].selected_seats[j];

                        var row = parseInt(string_oby.row);
                        var col = parseInt(string_oby.col);

                        console.log("PARSED selected_seats: row " + row + " col " + col);

                        console.log("ROW STRING " + string_oby.row);

                        console.log("ROW INT " + row);

                        $rootScope.buy.data_to_server.cart[i].selected_seats[j] = {
                            row: row,
                            col: col
                        };
                    }
                }

                console.log("COSA INVIO A DAVIDE");
                console.log("CART");
                console.log($rootScope.buy.data_to_server.cart);
                console.log("CREDIT CARD");
                console.log(ctrl.cd);

                // paga
                BuyProcedure.pay(ctrl.cd, $rootScope.buy.data_to_server.cart)
                    .success(function () {
                        $('.buy-loader').removeClass('active');
                        $rootScope.buy.complete_error = false;
                        $rootScope.cart = [];
                        $rootScope.buy.data_to_server = {};
                        $location.path('/buy_complete');
                    })
                    .error(function (data, status) {
                        if (status == 400) {
                            ctrl.error_msg = ctrl.ERROR_CARD;
                            $('.buy-loader').removeClass('active');
                        } else if (status == 409) {
                            $rootScope.cart = [];
                            $rootScope.cart = data;
                            $('.buy-loader').removeClass('active');
                            $rootScope.buy.complete_error = true;
                            $location.path('/buy_complete');
                        }
                    });

            };

            this.open_modal = function () {
                $('#modal_buy_cancel').openModal();
            };

            this.close_modal = function () {
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

            // calculate the total import to pay
            this.calcImportAndPrices = function () {
                var credit = $rootScope.user.credit;
                var cart = $rootScope.buy.data_to_server.cart;
                var total = -1;

                if (cart != undefined) {
                    total = 0;
                    for (var i = 0; i < cart.length; i++) {
                        for (var j = 0; j < $rootScope.tickets.length; j++) {
                            var num = 0;
                            for (var k = 0; k < cart[i].tickets.length; k++) {
                                if (cart[i].tickets[k].type == $rootScope.tickets[j].type) {
                                    num = num + cart[i].tickets[k].number;
                                    // set the price of a ticket
                                    cart[i].tickets[k].price = $rootScope.tickets[j].price * cart[i].tickets[k].number;
                                }
                            }
                            total = total + ($rootScope.tickets[j].price * num);
                        }
                    }
                }

                if (credit != undefined) {
                    if (total > credit) {
                        ctrl.importToPay = total - credit;
                    } else {
                        ctrl.importToPay = 0;
                    }
                } else {
                    ctrl.importToPay = total;
                }
            };


            ctrl.calcImportAndPrices();
        }])

        .controller('BuyCompleteController', ['$rootScope', function ($rootScope) {

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


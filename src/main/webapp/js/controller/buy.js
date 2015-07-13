(function () {
    'use strict';

    /* modulo per la gestione della procedura d'acquisto */
    angular.module('buyModule', ['usersModule', 'storageModule', 'constantsModule'])
        .controller('BuySeatController', ['$rootScope', '$location', '$anchorScroll', '$window', 'BuyProcedure', 'Rooms', function ($rootScope, $location, $anchorScroll, $window, BuyProcedure, Rooms) {

            var ctrl = this;

            this.go = false;
            this.error = "";

            // errori
            var ERROR = 1;
            var NOTHING = 2;

            // imposta un messaggio d'errore
            function setError(error) {
                switch (error) {
                    case ERROR :
                        ctrl.error = "Sembra che tu sia cercando di acquistare proiezioni o biglietti non più disponibili. Per favore, ricontrolla il carrello.";
                        break;
                    case NOTHING :
                        ctrl.error = "";
                        break;
                }
            }

            /* array di oggetti {row,col}. Gli oggetti contengono numero di riga e colonna dei posti selezionati per una proiezione*/
            $rootScope.buy.shared_obj.selected_seats = [];
            /* proiezione di cui si stanno attualmente selezionando i posti, contiene id, locandina, titolo, data-ora, num_posti */
            $rootScope.buy.shared_obj.film = {};

            /* funzione chiamata dal carrello quando l'utente vuole procedere con l'acquisto (pulsante procedi del carrello) */
            this.start_buy = function () {
                // init variabili
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

                    // reset di afterLogin (pagina su cui ssere redirezionati dopo il login)
                    $rootScope.afterLogin = "normal";
                    //verificare che il server dia l'ok  e che tutti i valori e spettacoli nel carrello siano giusti
                    BuyProcedure.proceed($rootScope.cart)
                        .success(function () {  // tutto ok
                            $('.buy-seats-loader').removeClass('active');
                            setError(NOTHING);
                            // copia i dati da usare durante la procedura
                            $rootScope.buy.data_from_server = ctrl.cloneObject($rootScope.cart);
                            console.log("successo, data from server ");
                            console.log($rootScope.buy.data_from_server);
                            // procedi
                            ctrl.go = true;
                            ctrl.next_buy();
                        })
                        .error(function (data) {    // biglietti o spettacoli non più disponibili
                            // ricarico il carrello aggiornato dal server
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

            /* per ogni spettacolo acquistato presenta la pagina di scelta dei posti. Rimanda poi alla pagina di riepilogo e conferma pagamento */
            this.next_buy = function () {

                // init
                $rootScope.buy.shared_obj.selected_seats = [];
                // prossima proiezione nel carrello
                $rootScope.buy.data_from_server_index++;

                if ($rootScope.buy.data_from_server_index < $rootScope.buy.data_from_server.length) {
                    /* ci sono ancora proiezioni per cui bisogna segliere i posti a sedere */

                    // reset messaggio
                    ctrl.seats_msg = "";

                    // imposta la proiezione corrente (della quale bisogna segliere i posti)
                    $rootScope.buy.shared_obj.film = $rootScope.buy.data_from_server[$rootScope.buy.data_from_server_index];

                    // conta il max di biglietti selezionabili
                    $rootScope.buy.shared_obj.film.seats_selected = 0;
                    for (var i = 0; i < $rootScope.buy.shared_obj.film.tickets.length; i++) {
                        $rootScope.buy.shared_obj.film.seats_selected = $rootScope.buy.shared_obj.film.seats_selected + $rootScope.buy.shared_obj.film.tickets[i].number;
                    }

                    // richiedi la sala
                    Rooms.getRoomStatus($rootScope.buy.shared_obj.film.pid)
                        .success(function (data) {
                            // init posti selezionati
                            $rootScope.buy.shared_obj.selected_seats = [];
                            // genera la sala
                            $rootScope.buy.shared_obj.film.seats = data.seats;
                        })
                        .error(function () {
                            // redirect alla pagina d'errore generale
                            $location.path("/error");
                        });

                    $('.buy-loader').removeClass('active');
                    // back to top of page
                    $anchorScroll();
                }
                else { /* scelta di tutti i posti di tutte le proiezioni terminata */
                    $('.buy-loader').removeClass('active');
                    // back to top of page
                    $anchorScroll();
                    // redirect alla pagina di riepilogo e conferma pagamento
                    $location.path('/buy_last_step');
                }


            };

            /* funzione chiamata dal bottone 'avanti' nella scelta posti. Salva i posti selezionati per una proiezione */
            this.save_seats = function () {

                // controllo se tutti i posti sono stati selezionati
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
                    // salvo l'oggetto nell'array che verrà inviato al server una volta completate le scelte dei posti di tutti gli spettacoli
                    $rootScope.buy.data_to_server.cart.push(ctrl.ff);
                    // prossima proiezione per cui selezionare i posti
                    this.next_buy();
                }

            };


            // apre il modal di conferma annullamento procedura
            this.open_modal = function () {
                $('#modal_buy_cancel').openModal();
            };

            1           // chiude il modal di conferma annullamento procedura
            this.close_modal = function () {
                $('#modal_buy_cancel').closeModal();
            };

            // cancella la procedura d'acquisto. Reinizializza le variabili correlate
            this.cancel_procedure = function () {
                $rootScope.buy.shared_obj.film = {};
                $rootScope.buy.shared_obj.selected_seats = [];
                $rootScope.buy.data_to_server.cart = [];
                $rootScope.buy.data_from_server = [];
                $rootScope.buy.data_from_server_index = -1;

                this.close_modal();
                $location.path('/today');
            };

            // copia un oggetto
            this.cloneObject = function (obj) {
                return (JSON.parse(JSON.stringify(obj)));
            };

            // inizia la procedura d'acquisto
            this.start_buy();

        }])

        // controller pagina di riepilogo acquisto
        .controller('BuySummaryController', ['$rootScope', '$location', 'BuyProcedure', function ($rootScope, $location, BuyProcedure) {

            var ctrl = this;
            this.cd = { // carta di credito
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

            // chiede la conferma dell'acqwuisto al server e invia i dati di pagamento e il posti prenotati
            this.pay = function () {
                $('.buy-loader').addClass('active');
                ctrl.error_msg = "";

                // parsing (string->int) dei posti prenotati
                for (var i = 0; i < $rootScope.buy.data_to_server.cart.length; i++) {

                    for (var j = 0; j < $rootScope.buy.data_to_server.cart[i].selected_seats.length; j++) {
                        var string_oby = $rootScope.buy.data_to_server.cart[i].selected_seats[j];

                        var row = parseInt(string_oby.row);
                        var col = parseInt(string_oby.col);

                        // aggiorna l'oggetto
                        $rootScope.buy.data_to_server.cart[i].selected_seats[j] = {
                            row: row,
                            col: col
                        };
                    }
                }


                // invio richiesta con riepilogo acquisto e carta di credito
                BuyProcedure.pay(ctrl.cd, $rootScope.buy.data_to_server.cart)
                    .success(function () {
                        $('.buy-loader').removeClass('active');
                        $rootScope.buy.complete_error = false;
                        $rootScope.cart = [];
                        $rootScope.buy.data_to_server = {};
                        $location.path('/buy_complete');
                    })
                    .error(function (data, status) {
                        if (status == 400) {    // errore dati carta di credito
                            ctrl.error_msg = ctrl.ERROR_CARD;
                            $('.buy-loader').removeClass('active');
                        } else if (status == 409) { // errore dati dell'acquisto (proiezione o posti non più disponibili)
                            // ricarico il carrello aggiornato dal server
                            $rootScope.cart = [];
                            $rootScope.cart = data;
                            // redirect pagina errore
                            $('.buy-loader').removeClass('active');
                            $rootScope.buy.complete_error = true;
                            $location.path('/buy_complete');
                        }
                    });
            };

            // apre il modal di conferma annullamento procedura
            this.open_modal = function () {
                $('#modal_buy_cancel').openModal();
            };

            // chiude il modal di conferma annullamento procedura
            this.close_modal = function () {
                $('#modal_buy_cancel').closeModal();
            };

            // cancella la procedura d'acquisto. Reinizializza le variabili correlate
            this.cancel_procedure = function () {
                $rootScope.buy.shared_obj.film = {};
                $rootScope.buy.shared_obj.selected_seats = [];
                $rootScope.buy.data_to_server = {};
                $rootScope.buy.data_from_server = [];
                $rootScope.buy.data_from_server_index = -1;

                this.close_modal();
                $location.path('/today');
            };

            // calcola l'importo da pagare e i prezzi in dettaglio
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
                                    // imposta il prezzo dei biglietti di una tipologia
                                    cart[i].tickets[k].price = $rootScope.tickets[j].price * cart[i].tickets[k].number;
                                }
                            }
                            total = total + ($rootScope.tickets[j].price * num);
                        }
                    }
                }

                // calcola il totale tenendo conto del credito utente
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

        // controller pagina di procedura completata
        .controller('BuyCompleteController', ['$rootScope', function ($rootScope) {

            this.BUY_COMPLETE_SUCCESS = "Il pagamento è andato a buon fine. Grazie per aver acquistato sul nostro sito! A breve riceverai i biglietti direttamente nella tua casella di posta.";
            this.BUY_COMPLETE_ERROR = "Oh oh... Sembra che qualcuno abbia già prenotato i posti che hai selezionato o hai impiegato troppo a completare la procedura d'acquisto. Ti invitiamo a riprovare.";
            this.buy_complete_msg = "";

            // imposta il messaggio in base al successo della procedura d'acquisto
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


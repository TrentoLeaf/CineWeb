(function () {
    'use strict';

    angular.module('buyModule', ['usersModule', 'storageModule', 'constantsModule'])
        .controller('BuySeatController', ['$rootScope', '$location', '$anchorScroll', function ($rootScope, $location, $anchorScroll) {

            var ctrl = this;


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

                console.log("start index: " + $rootScope.buy.data_from_server_index);
                console.log($rootScope.buy.shared_obj.film);

                // TODO controlla se è loggato
                /* se loggato prosegui col codice, altrimenti redirect alla pagina di login*/

                // TODO chiamata AJAX per inivare dati di acquisto e poter procedere
                // TODO callback
                // TODO salva i dati ricevuti dal server in data_from_server
                // lancia procedura scelta biglietti
                this.next_buy();



                // TODO errore
                /* redirect pagina d'errore */
            };

            /* per ogni spettacolo acquistato presenta la pagina di scelta dei posti. Rimanda poi alla pagina di riepilogo*/
            this.next_buy = function () {

                $rootScope.buy.shared_obj.selected_seats = [];
                // next film in cart
                console.log("here1: " + $rootScope.buy.data_from_server_index);
                $rootScope.buy.data_from_server_index++;
                console.log("here2: " + $rootScope.buy.data_from_server_index);
                if ($rootScope.buy.data_from_server_index < $rootScope.buy.data_from_server.length) {

                    $rootScope.buy.shared_obj.film = $rootScope.buy.data_from_server[$rootScope.buy.data_from_server_index];

                    console.log("next index: " + $rootScope.buy.data_from_server_index);
                    console.log($rootScope.buy.shared_obj.film);

                }
                else { // scelta dei posti terminata
                    // TODO chiamata AJAX con invio di data_to_server
                    // TODO se il server risponde 'tutto ok'
                    // Redirect alla pagina di riepilogo
                    $location.path('/buy_last_step');
                    // TODO server risponde errore --> gestire il tipo  di errore
                }

                $('.buy-loader').removeClass('active');
                // back to top of page
                $location.hash('main-content');
                $anchorScroll();
            };

            /* chiamata da bottone avanti nella scelta posti */
            this.save_seats = function () {
                $('.buy-loader').addClass('active');
                // creo un nuovo oggetto
                this.ff = this.cloneObject($rootScope.buy.shared_obj.film);
                // aggiungo allo spettacolo i posti selezionati
                this.ff.selected_seats = $rootScope.buy.shared_obj.selected_seats;
                // salvo l'oggetto nell'array che inverò al server una volta completate le scelte dei posti di tutti gli spettacoli
                $rootScope.buy.data_to_server.push(this.ff);
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
                $rootScope.buy.data_to_server = [];
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

        .controller('BuySummaryController', ['$rootScope', '$location', function ($rootScope, $location) {

            // messaggi
            this.error_msg = "";
            this.ERROR_CARD = "I dati inseriti sembrano non essere validi. Controlla.";
            this.pay = function () {
                $('.buy-loader').addClass('active');


                /*TODO: in callback function:
                 $('.buy-loader').removeClass('active');
                 $rootScope.buy.complete_error = false;
                 $rootScope.cart = [];
                 $location.path('/buy_complete');
                 */
                /*TODO: in callback error function:
                 -> errore carta di credito
                 this.error_msg = this.ERROR_CARD;
                 $('.buy-loader').removeClass('active');
                 -> errore posti
                 $('.buy-loader').removeClass('active');
                 $rootScope.buy.complete_error = true;
                 $location.path('/buy_complete');
                 */

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
                $rootScope.buy.data_to_server = [];
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


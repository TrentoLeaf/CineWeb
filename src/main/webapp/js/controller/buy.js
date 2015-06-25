(function () {
    'use strict';

    angular.module('buyModule', ['usersModule', 'storageModule', 'constantsModule'])
        .controller('BuyController', ['$location', '$anchorScroll', function ($location, $anchorScroll) {

            this.shared_obj = this.shared_obj || {};

            this.data_from_server = [ {
                title: "titolo",
                date: "data",
                time: "ora",
                playbill: "img/temporary/mad-max-fury-road-locandina-400x250.jpg",
                seats: [
                    [1, 1, 1, 0, 1, 1, 1],
                    [1, 0, 1, 2, 0, 1, 1],
                    [1, 2, 1, 2, 1, 2, 1],
                    [1, 1, 1, 1, 1, 2, 0]
                ],
                seats_selected: 4
            },
                {
                    title: "titolo2",
                    date: "data2",
                    time: "ora2",
                    playbill: "img/temporary/mad-max-fury-road-locandina-400x250.jpg",
                    seats: [
                        [1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 2, 1, 2],
                        [1, 1, 0, 0, 1, 1, 1, 1, 0, 1, 2, 2, 1, 2],
                        [1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 2, 1, 2],
                        [1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 2, 1, 2],
                        [1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 2, 1, 2],
                        [1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 2, 1, 2],
                        [1, 1, 0, 0, 1, 1, 1, 1, 0, 1, 2, 2, 1, 2],
                        [1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 2, 1, 2],
                        [1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 2, 1, 2],
                        [1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 2, 1, 2],
                        [1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 2, 1, 2],
                        [1, 1, 0, 0, 1, 1, 1, 1, 0, 1, 2, 2, 1, 2],
                        [1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 2, 1, 2],
                        [1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 2, 1, 2],
                        [1, 1, 0, 0, 1, 1, 1, 1, 0, 1, 2, 2, 1, 2],
                        [1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 2, 1, 2],
                        [1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 2, 1, 2]
                    ],
                    seats_selected: 6
                }];

            this.data_from_server_index = -1;
            this.data_to_server = [];
            this.shared_obj.film = {};
            /* film di cui si stanno attualmente selezionando i posti, contiene id, locandina, titolo, data-ora, num_posti */
            this.shared_obj.selected_seats = [];
            /* array di oggetti. Gli oggetti sono i posti selezionati */
            this.error_msg = "";


            /* chiamata dal carrello quando l'utente vuole procedere con l'acquisto (pulsante procedi del carrello)*/
            this.start_buy = function () {
                this.shared_obj.film = {};
                this.shared_obj.selected_seats = [];
                this.data_to_server = [];
                this.data_from_server_index = -1;

                console.log("start index: " + this.data_from_server_index);
                console.log(this.shared_obj.film);

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

                this.shared_obj.selected_seats = [];
                // next film in cart
                console.log("here1: " + this.data_from_server_index);
                this.data_from_server_index++;
                console.log("here2: " + this.data_from_server_index);
                if (this.data_from_server_index < this.data_from_server.length) {

                    this.shared_obj.film = this.data_from_server[this.data_from_server_index];

                    console.log("next index: " + this.data_from_server_index);
                    console.log(this.shared_obj.film);

                 }
                else { // scelta dei posti terminata
                    // TODO chiamata AJAX con invio di data_to_server
                    // TODO se il server risponde 'tutto ok'
                    // Redirect alla pagina di riepilogo
                    $location.path('/buy_last_step');
                    // TODO server risponde errore --> gestire il tipo  di errore
                }

                // back to top of page
                $location.hash('main-content');
                $anchorScroll();
            };

            /* chiamata da bottone avanti nella scelta posti */
            this.save_seats = function () {
                // creo un nuovo oggetto
                this.ff = this.cloneObject(film);
                // aggiungo allo spettacolo i posti selezionati
                this.ff.selected_seats = this.shared_obj.selected_seats;
                // salvo l'oggetto nell'array che inverò al server una volta completate le scelte dei posti di tutti gli spettacoli
                data_to_server.push(this.ff);
                this.next_buy();
            };


            this.open_modal = function () {
                $('#modal_buy_cancel').openModal();
            };

            this.close_modal = function() {
                $('#modal_buy_cancel').closeModal();
            };

            this.cancel_procedure = function () {
                this.shared_obj.film = {};
                this.shared_obj.selected_seats = [];
                this.data_to_server = [];
                this.data_from_server = [];
                this.data_from_server_index = -1;

                console.log("buy procedure canceled!");
                this.close_modal();
                $location.path('/today');
            };

            this.cloneObject = function (obj) {
                return (JSON.parse(JSON.stringify(obj)));
            };


            this.start_buy();

        }]);
})();


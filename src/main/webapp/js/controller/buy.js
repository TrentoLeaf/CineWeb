var svg_string_stage = "<g class='stage'><polygon class='stage_floor' points='675,0 5,0 0,140 680,140'/><polygon class='stage_step' points='665,165 15,165 0,140 680,140'/><g class='stage_screen'><path class='stage_screen_display' d='M531,105c-0.9,2.5-2.5,5-5.6,5H154.9c-3.1,0-5-2.5-5.6-5L115,35h450L531,105z'/><path class='stage_screen_depth' d='M565,35c0-2.8-2.5-5-5.6-5H120.6c-3.1,0-5.6,2.2-5.6,5l0,0c0,2.8,2.5,5,5.6,5h438.8 C562.5,40,565,37.8,565,35L565,35z'/></g></g>";
var svg_string_theatre = "<rect x='0' y='0' class='theatre' width='650' height='385'/>";
var svg_string_seat = "<g class='seat'><rect x='0' y='0' class='seat_rectangle' width='55' height='55'/><g class='seat_armchair'><path class='seat_back' d='M44.2,41.8c0,2.3-0.5,4.7-1.4,5.7H12.2c-0.9-1-1.4-3.3-1.4-5.7c0-2.3,0.5-4.6,1.4-5.6h30.6 c0.2,0.2,0.3,0.4,0.5,0.7c0.1,0.3,0.3,0.6,0.4,0.9c0.1,0.4,0.3,1,0.4,1.5C44.2,40.1,44.2,41,44.2,41.8z'/><path class='seat_arm' d='M12.2,10.7c-2.4-0.6-6-0.2-7.2,1v20.4c0,10.1,5.8,9.8,5.8,9.8c0-2.3,0.5-4.6,1.4-5.6h2V11.7	C13.8,11.3,13.1,10.9,12.2,10.7z'/><path class='seat_arm' d='M42.8,10.7c-0.9,0.2-1.6,0.6-2,1v24.5h2c0.2,0.2,0.3,0.4,0.5,0.7c0.1,0.3,0.3,0.6,0.4,0.9 c0.1,0.5,0.3,0.9,0.4,1.5c0.1,0.8,0.2,1.7,0.2,2.5c0,0,5.8,0.3,5.8-9.8V11.7C48.8,10.5,45.2,10.1,42.8,10.7z'/><path class='seat_sitting' d='M42.8,10.5v0.2c-0.9,0.2-1.6,0.6-2,1v24.5H14.2V11.7c-0.4-0.5-1.1-0.8-2-1v-0.2C17.6,6.5,37.4,6.5,42.8,10.5z'/></g></g>";
var svg_string_row_headers = "<g class='theatre_row_header'><rect x='0' y='0' class='theatre_row_header_rectangle' width='50' height='55'/><text x='25' y='35' class='theatre_row_header_text'>A</text></g>";
var svg_string_column_headers = "<g class='theatre_column_header'><rect x='0' y='0' class='theatre_column_header_rectangle' width='55' height='50'/><text x='27.5' y='33' class='theatre_column_header_text'>1</text></g>";


var SEAT_NOT_EXIST = 0;
var SEAT_AVAILABLE = 1;
var SEAT_UNAVAILABLE = 2;

(function () {
    'use strict';

    angular.module('buyModule', ['usersModule', 'storageModule', 'constantsModule'])
        .controller('BuyController', ['$location', '$anchorScroll', function ($location, $anchorScroll) {


            this.data_from_server = [{title:"titolo", date:"data", time:"ora", playbill:"img/temporary/mad-max-fury-road-locandina-400x250.jpg", seats:[[1,1,1,0,1,1,1],[1,0,1,2,0,1,1],[1,2,1,2,1,2,1],[1,1,1,1,1,2,0]], seats_selected: 4},
                {title:"titolo2", date:"data2", time:"ora2", playbill:"img/temporary/mad-max-fury-road-locandina-400x250.jpg", seats:[[1,1,1,0,1,1,1,0],[1,0,1,2,0,1,1,1],[1,2,1,2,1,2,1,0],[1,1,1,1,2,2,0,0],[1,0,1,2,0,1,1,1]], seats_selected: 6}];
            this.data_from_server_index = -1;
            this.data_to_server = [];
            this.seats2 = [[1,1,1,0,1,1,1],[1,0,1,2,0,1,1],[1,2,1,2,1,2,1],[1,1,1,1,1,2,0]]; /* remove */
            this.film = {}; /* film di cui si stanno attualmente selezionando i posti, contiene id, locandina, titolo, data-ora, num_posti */
            this.selected_seats = []; /* array di oggetti. Gli oggetti sono i posti selezionati */
            this.error_msg = "";

            /* chiamata dal carrello quando l'utente vuole procedere con l'acquisto (pulsante procedi del carrello)*/
            this.start_buy = function () {
                this.film = {};
                this.selected_seats = [];
                this.data_to_server = [];
                this.data_from_server_index = -1;

                console.log("start index: "+ this.data_from_server_index);
                console.log(this.film);

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

                this.selected_seats = [];
                // next film in cart
                console.log("here1: "+ this.data_from_server_index);
                this.data_from_server_index ++;
                console.log("here2: "+ this.data_from_server_index);
                if (this.data_from_server_index < this.data_from_server.length) {
                    $location.path('/buy');
                    this.film = this.data_from_server[this.data_from_server_index];

                    console.log("next index: "+ this.data_from_server_index);
                    console.log(this.film);

                    // generate svg seats
                    generateSvg(this.data_from_server[this.data_from_server_index].seats, this.selected_seats, this.film);
                    // back to top of page
                    $location.hash('main-content');
                    $anchorScroll();
                }
                else { // scelta dei posti terminata
                    // TODO chiamata AJAX con invio di data_to_server
                    // TODO se il server risponde 'tutto ok'
                    // Redirect alla pagina di riepilogo
                    $location.path('/buy_last_step');
                    // TODO server risponde errore --> gestire il tipo  di errore
                }
            };

            /* chiamata da bottone avanti nella scelta posti */
            this.save_seats = function () {
                // creo un nuovo oggetto
                this.ff = this.cloneObject(film);
                // aggiungo allo spettacolo i posti selezionati
                this.ff.selected_seats = this.selected_seats;
                // salvo l'oggetto nell'array che inverò al server una volta completate le scelte dei posti di tutti gli spettacoli
                data_to_server.push(this.ff);
                this.next_buy();
            };

            this.cloneObject = function (obj) {
                return (JSON.parse(JSON.stringify(obj)));
            };


        }]);
})();


/*  functions to manage the svg */

function generateSvg (posti, posti_selezionati, film) {
    // aspetta che il dom sia pronto
    $(document).ready(function() {

        var rows = posti.length;
        var columns = posti[0].length;
        // calculate dimensions

        // theatre diimensions
        var theatre_w = 50 + (55 * columns) + 50;
        var theatre_h = 60 + (55 * rows) + 50;
        // stage dimensions
        var stage_w = (680 * theatre_w) / 650;
        var stage_h = 165;
        // theatre positions
        var theatre_x = ((15 * stage_w) / 680);
        var theatre_y = 165;
        // set viewbox dimensions

        // $("#svg-theatre")[0].setAttribute("viewBox", '0 0 '+stage_w+' '+(stage_h+theatre_h)); // leave has it is due to a jQuery/SnapSVG bug
        $("#svg-theatre").attr({width: "100%", height: "100%"});
        // init snap
        var snap = Snap("#svg-theatre");

        console.log($("#svg-theatre"));
        console.log(Snap("#svg-theatre"));


        snap.attr({viewBox: '0 0 '+stage_w+' '+(stage_h+theatre_h)});

        // load SVG objects fragments
        var fragment_stage = Snap.fragment(svg_string_stage);
        var fragment_theatre = Snap.fragment(svg_string_theatre);
        var fragment_seat = Snap.fragment(svg_string_seat);
        var fragment_row_header = Snap.fragment(svg_string_row_headers);
        var fragment_column_header = Snap.fragment(svg_string_column_headers);

        // set the objects variables
        var stage = fragment_stage.select(".stage");
        var theatre = fragment_theatre.select(".theatre");
        var seat = fragment_seat.select(".seat");
        var row_header = fragment_row_header.select(".theatre_row_header");
        var column_header = fragment_column_header.select(".theatre_column_header");

        // set dimensions of theatre and append to snap
        stage.transform('s' + (stage_w / 680) + ',1'); // scale the stage
        theatre.attr({width: theatre_w, height: theatre_h, x: theatre_x, y: theatre_y});
        snap.append(stage);
        snap.append(theatre);

        // variable for seats and headings postioning
        var i_index = theatre_x;
        var j_index = theatre_y + 60;
        var c = String.fromCharCode('A'.charCodeAt() - 1);
        var num = 0;

        // set all the seats
        for (var i = 0; i < rows; i++) {

            // print row header
            var rr = row_header.clone();
            rr.transform('t' + i_index + ',' + j_index); // set traslation
            c = nextChar(c);
            rr.select('text').attr({text: c}); // set letter
            snap.append(rr);

            // transalte to next column
            i_index += 50;

            // print the seats in row
            for (var j = 0; j < columns; j++) {
                // check if seat exist
                if (posti[i][j] != SEAT_NOT_EXIST) {

                    // get a seat
                    var pp = seat.clone();
                    pp.transform('t' + i_index + ',' + j_index); // set traslation
                    pp.attr({row: i.toString()});
                    pp.attr({col: j.toString()});

                    switch (posti[i][j]) {

                        case SEAT_AVAILABLE:
                            // set hover colors
                            pp.hover(seatHoverIn, seatHoverOut);

                            // set click handler
                            pp.click(function () {
                                if (this.hasClass('seat-selected')) {
                                    this.removeClass('seat-selected');
                                    this.hover(seatHoverIn, seatHoverOut);
                                    // TODO remove the id from list of selected seats
                                    posti_selezionati.splice({
                                        row: pp.attr('row'),
                                        col: pp.attr('col')
                                    }, 1);
                                    film.seats_selected++;
                                }
                                else {
                                    // TODO check if can be selected (eg: no more seat selactable)
                                    if (film.seats_selected > 0) {
                                        this.addClass('seat-selected');
                                        this.removeClass('seat-hover');
                                        this.unhover(seatHoverIn, seatHoverOut);
                                        // TODO add the id to list of selected seats
                                        posti_selezionati.push({
                                            row: pp.attr('row'),
                                            col: pp.attr('col')
                                        });
                                        film.seats_selected--;
                                    }
                                }
                            });
                            break;
                        case SEAT_UNAVAILABLE:
                            // set color
                            pp.addClass('seat-unavailable');
                            break;
                    }

                    snap.append(pp);
                }

                // next seat
                i_index += 55;
            }

            // move to next row
            j_index += 55;
            i_index = theatre_x;
        }

        // print column headers

        // traslate to first seat column
        i_index += 50;

        for (var i = 0; i < columns; i++) {
            // print column header
            var cc = column_header.clone();
            cc.transform('t' + i_index + ',' + j_index); // set traslation
            num++;
            cc.select('text').attr({text: num}); // set number
            snap.append(cc);
            // next header
            i_index += 55;
        }

    });
}

/* return the next character in aphabet relative to char input */
function nextChar(c) {
    if (c == 'Z') {
        return 'A';
    }
    return String.fromCharCode(c.charCodeAt() + 1);
}

function seatHoverIn() {
    this.addClass('seat-hover');
}

function seatHoverOut () {
    this.removeClass('seat-hover');
}
(function () {
    'use strict';

    var svg_string_stage = "<g class='stage'><polygon class='stage_floor' points='675,0 5,0 0,140 680,140'/><polygon class='stage_step' points='665,165 15,165 0,140 680,140'/><g class='stage_screen'><path class='stage_screen_display' d='M531,105c-0.9,2.5-2.5,5-5.6,5H154.9c-3.1,0-5-2.5-5.6-5L115,35h450L531,105z'/><path class='stage_screen_depth' d='M565,35c0-2.8-2.5-5-5.6-5H120.6c-3.1,0-5.6,2.2-5.6,5l0,0c0,2.8,2.5,5,5.6,5h438.8 C562.5,40,565,37.8,565,35L565,35z'/></g></g>";
    var svg_string_theatre = "<rect x='0' y='0' class='theatre' width='650' height='385'/>";
    var svg_string_seat = "<g class='seat'><rect x='0' y='0' class='seat_rectangle' width='55' height='55'/><g class='seat_armchair'><path class='seat_back' d='M44.2,41.8c0,2.3-0.5,4.7-1.4,5.7H12.2c-0.9-1-1.4-3.3-1.4-5.7c0-2.3,0.5-4.6,1.4-5.6h30.6 c0.2,0.2,0.3,0.4,0.5,0.7c0.1,0.3,0.3,0.6,0.4,0.9c0.1,0.4,0.3,1,0.4,1.5C44.2,40.1,44.2,41,44.2,41.8z'/><path class='seat_arm' d='M12.2,10.7c-2.4-0.6-6-0.2-7.2,1v20.4c0,10.1,5.8,9.8,5.8,9.8c0-2.3,0.5-4.6,1.4-5.6h2V11.7	C13.8,11.3,13.1,10.9,12.2,10.7z'/><path class='seat_arm' d='M42.8,10.7c-0.9,0.2-1.6,0.6-2,1v24.5h2c0.2,0.2,0.3,0.4,0.5,0.7c0.1,0.3,0.3,0.6,0.4,0.9 c0.1,0.5,0.3,0.9,0.4,1.5c0.1,0.8,0.2,1.7,0.2,2.5c0,0,5.8,0.3,5.8-9.8V11.7C48.8,10.5,45.2,10.1,42.8,10.7z'/><path class='seat_sitting' d='M42.8,10.5v0.2c-0.9,0.2-1.6,0.6-2,1v24.5H14.2V11.7c-0.4-0.5-1.1-0.8-2-1v-0.2C17.6,6.5,37.4,6.5,42.8,10.5z'/></g></g>";
    var svg_string_row_headers = "<g class='theatre_row_header'><rect x='0' y='0' class='theatre_row_header_rectangle' width='50' height='55'/><text x='25' y='35' class='theatre_row_header_text'>A</text></g>";
    var svg_string_column_headers = "<g class='theatre_column_header'><rect x='0' y='0' class='theatre_column_header_rectangle' width='55' height='50'/><text x='27.5' y='33' class='theatre_column_header_text'>1</text></g>";

    var SEAT_NOT_EXIST = 0,
        SEAT_AVAILABLE = 1,
        SEAT_UNAVAILABLE = 2,
        SEAT_BEST = 3;

    /*  functions to manage the svg */

    function generateSvg(mappa, spettacolo, posti_selezionati) {
        /* INPUT
         Tutti)
         mappa: mappa della sala
         1) Mappa selezione posti (selection)
         spettacolo: oggetto contente i dati relativi ad uno spettacolo acquistato (seats_selected)
         posti_selezionati: array da riempire con i posti selezionati nell sala
         2) Lista posti già acquistati
         mappa: matrice con posti acquistati (uguali a SEAT_UNAVAILABLE)
         3) Posti migliori (best_seats)
         mappa: matrice con posti migiliori (uguali a SEAT_BEST)
         NOTA: variabili non usate, alla chiamata,  possono essere messe a null
         */

        var rows = mappa.length;
        var columns = mappa[0].length;
        // calculate dimensions

        // theatre dimensions
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


        snap.attr({viewBox: '0 0 ' + stage_w + ' ' + (stage_h + theatre_h)});

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

            // translate to next column
            i_index += 50;

            // print the seats in row
            for (var j = 0; j < columns; j++) {
                // check if seat exist
                if (mappa[i][j] != SEAT_NOT_EXIST) {

                    // get a seat
                    var pp = seat.clone();
                    pp.transform('t' + i_index + ',' + j_index); // set traslation
                    pp.attr({row: i.toString()});
                    pp.attr({col: j.toString()});

                    // set the type of seat
                    set_svg_seat(pp, mappa[i][j], spettacolo, posti_selezionati);

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
    }

    function set_svg_seat (poltrona_svg, posto, spettacolo, posti_selezionati) {

        switch (posto) {

            case SEAT_AVAILABLE:
                if (spettacolo != undefined) { // only for selection

                    // set hover colors
                    poltrona_svg.hover(seatHoverIn, seatHoverOut);

                    // set click handler
                    poltrona_svg.click(function () {
                        if (this.hasClass('seat-selected')) {
                            this.removeClass('seat-selected');
                            this.hover(seatHoverIn, seatHoverOut);
                            // TODO remove the id from list of selected seats
                            posti_selezionati.splice({
                                row: poltrona_svg.attr('row'),
                                col: poltrona_svg.attr('col')
                            }, 1);
                            spettacolo.seats_selected++;
                        }
                        else {

                            if (spettacolo.seats_selected > 0) {
                                this.addClass('seat-selected');
                                this.removeClass('seat-hover');
                                this.unhover(seatHoverIn, seatHoverOut);

                                posti_selezionati.push({
                                    row: poltrona_svg.attr('row'),
                                    col: poltrona_svg.attr('col')
                                });
                                spettacolo.seats_selected--;
                            }
                        }
                    });
                }
                break;
            case SEAT_UNAVAILABLE:
                // set color
                poltrona_svg.addClass('seat-unavailable');
                break;
            case  SEAT_BEST:
                // set color
                poltrona_svg.addClass('seat-best');
                break;
        }
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

    function seatHoverOut() {
        this.removeClass('seat-hover');
    }

    // angular directive
    angular.module('mapModule', [])
        .directive('map', function () {
            return {
                restrict: 'E',
                template: '<div id="svg-container"><svg id="svg-theatre"></svg></div>',
                scope: {
                    o: '=o'
                },
                link: function (scope, element, attrs) {
                    // gestione della mappa per la selezione dei posti
                    scope.$watch('o.film.seats', function (seats) {
                        // seats == scope.o.seats
                        console.log(seats);
                        console.log(scope.seats);

                        //NEW
                        if ((scope.o.film != undefined) && (scope.o.film.seats != undefined) && (scope.o.selected_seats != undefined)) {
                            generateSvg(scope.o.film.seats, scope.o.film, scope.o.selected_seats);
                        }

                    }, true);

                    // gestione della mappa per i posti occupati di una sala e i posti migliori
                    scope.$watch('o.mapTheatre', function (map) {

                        // scope.o.mapTheatre: mappa (matrice) di una sala
                        if ((scope.o.mapTheatre != undefined)) {
                            generateSvg(scope.o.mapTheatre, undefined, undefined);
                        }

                    }, true);

                }
            }
        })

        .controller('MapController', function () {
            // TODO serve questo controller?
            this.test = {
                a: 123,
                b: "pippo",
                c: [1, 4, 5]
            };

            this.do = function () {
                this.test.b = "pluto";
                this.test.d = this.test.d || [];
                this.test.d.push(21);
            };
        });

})();
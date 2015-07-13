(function () {
    'use strict';

    /* modulo gestione film (admin)*/
    angular.module('adminFilms', ['filmsPlaysModule'])

        /* controller per la modifica di un film */
        .controller('AdminFilmsEditController', ['$routeParams', '$location', 'Films', function($routeParams, $location, Films) {

            var ctrl = this;
            this.currentFilm = {};
            this.status = "";
            this.error_message = false;

            // carica il film da modificare
            Films.get({id:$routeParams.fid}).$promise.then(function (data) {
                ctrl.currentFilm = data;
            }, function () {
                $location.path("/admin/films");
            });


            // salva il film modificato nel db
            this.save = function () {
                Films.update({id: ctrl.currentFilm.fid}, ctrl.currentFilm).$promise.then(function (data) {
                    // ok
                    console.log("UPDATE OK ->");
                    console.log(data);
                    $location.path("/admin/films")
                }, function (response) {
                    // fail...
                    ctrl.error_message = true;
                    ctrl.setStatus("Qualcosa è andato storto, riprova");
                    $("html, body").animate({ scrollTop: 0 }, "fast");
                });
            };

            // imposta un messaggio
            this.setStatus = function (status) {
                ctrl.setStatusClass();
                ctrl.status = status;
            };

            // impost il tipo di messaggio
            this.setStatusClass = function () {
                if(ctrl.error_message) {
                    $('#film_edit_message').removeClass("green-text white-text");
                    $('#film_edit_message').addClass("red-text");
                } else {
                    $('#film_edit_message').removeClass("red-text white-text");
                    $('#film_edit_message').addClass("green-text");
                }
            };

        }])
        /* controller per la gestione dei film e la creazone di un nuovo film */
        .controller('AdminFilmsController', ['$rootScope', '$location', 'Films', function ($rootScope, $location, Films) {


            var ctrl = this;
            this.newFilm = new Films();
            this.status = "";
            this.error_message = false;


            var init = function () {
                if ($rootScope.isUserLogged == false) {
                    $rootScope.afterLogin = "userArea";
                    $location.path('/login');
                }

                ctrl.loading = true;
                ctrl.films = [];
            };

            // carica i films
            this.loadFilms = function () {

                init();

                Films.query(function (data) {
                    ctrl.films = data;
                    ctrl.loading = false;
                }, function (){
                    //Fail Case
                    ctrl.error_message = true;
                    ctrl.setStatus("Errore durante il caricamento dei film");
                });
            };

            // aggiunge un nuovo film al db
            this.addFilm = function () {

                ctrl.newFilm.$save( function (data) {
                    ctrl.films.push(data);
                    ctrl.newFilm = new Films();
                    console.log("Film insertion success");
                    $location.path("/admin/films");

                }, function (response) {
                    //Fail Case
                    if (response.status == 409) {
                        ctrl.error_message = true;
                        ctrl.setStatus("Film già esistente");
                    } else if (response.status == 400) {
                        ctrl.error_message = true;
                        ctrl.setStatus("Controlla i dati inseriti");
                    } else {
                        ctrl.error_message = true;
                        ctrl.setStatus("Qualcosa è andato storto, riprova");
                    }
                    $("html, body").animate({ scrollTop: 0 }, "fast");
                });
            };

            // cancella un film
            this.deleteFilm = function () {
                Films.delete({id: ctrl.tmpFilm.fid}, function (){
                    console.log("Film deletion success");
                    ctrl.error_message = false;
                    ctrl.setStatus("Film cancellato con successo");
                    $("html, body").animate({ scrollTop: 0 }, "fast");
                }, function (response) {
                    if (response.status == 409) {
                        ctrl.error_message = true;
                        ctrl.setStatus("Impossibile cancellare il film, è attualmente in proiezione");
                    } else if (response.status == 400){
                        ctrl.error_message = true;
                        ctrl.setStatus("Impossibile procedere alla cancellazione, film non trovato");
                    } else {
                        ctrl.error_message = true;
                        ctrl.setStatus("Qualcosa è andato storto, riprova");
                    }
                    $("html, body").animate({ scrollTop: 0 }, "fast");
                });

            };

            // apre modal conferma eliminazione
            this.open_modal = function (index) {
                this.tmpFilm = this.films[index];
                $('#modal_film_delete').openModal();
            };

            // chiude modal conferma eliminazione
            this.close_modal = function () {
                $('#modal_film_delete').closeModal();
            };

            // imposta un messaggio
            this.setStatus = function (status) {
                ctrl.setStatusClass();
                ctrl.status = status;
            };

            // imposta il tipo di messaggio
            this.setStatusClass = function () {
                if(ctrl.error_message) {
                    $('#film_message').removeClass("green-text white-text");
                    $('#film_message').addClass("red-text");
                } else {
                    $('#film_message').removeClass("red-text white-text");
                    $('#film_message').addClass("green-text");
                }
            };

            this.loadFilms();
        }]);


})();

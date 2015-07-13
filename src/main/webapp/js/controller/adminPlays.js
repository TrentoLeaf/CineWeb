(function () {
    'use strict';

    /* modulo per la gestione delle proiezioni (admin) */
    angular.module('adminPlays', ['filmsPlaysModule'])
        /* controller per un a nuova proiezione */
        .controller ('AdminNewPlaysController', ['Films', '$rootScope', '$scope', '$location', 'Plays', function (Films, $rootScope, $scope, $location, Plays) {

        var ctrl = this;
        this.status = "";
        this.error_message = false;
        this.newPlay =  new Plays();
        this.newPlay_3d = false;
        this.films = [];

        this.date = "";
        this.time = "21:00"; // NB: tiene conto solo dell'orario

        // carica tutti i film disponibili
        this.loadFilms = function () {

            Films.query(function (data) {
                ctrl.films = data;
                console.log(ctrl.films);
            }, function (){
                ctrl.error_message = true;
                ctrl.setStatus("Errore durante il caricamento dei film");
            });
        };

        // invia richiesta nuova proiezione
        this.addPlay = function (data) {

            console.log("data: " + ctrl.date);
            console.log("tempo: " + ctrl.time);

            // set the complete time (composition of ctrl.date + ctrl.time (taking only hours and minutes))
            var new_time = new Date(ctrl.date + " " + ctrl.time);
            console.log("newdata: " + new_time);
            ctrl.newPlay.time = new_time;

            if (ctrl.newPlay_3d) {
                ctrl.newPlay._3d = true;
            } else {
                ctrl.newPlay._3d = false;
            }

            ctrl.newPlay.$save(function (data) {
                ctrl.newPlay = new Plays();
                console.log("Play insertion success");
                ctrl.updatePlays();
                $location.path("/admin/plays");

            }, function (response) {
                if(response.status == 409) {
                    ctrl.error_message = true;
                    ctrl.setStatus("Esiste già una proiezione in questa sala per questo preciso orario");
                } else if (response.status == 400) {
                    ctrl.error_message = true;
                    ctrl.setStatus("I dati inseriti non sono corretti, riprova");
                } else {
                    ctrl.error_message = true;
                    ctrl.setStatus("Qualcosa è andato storto, riprova");
                }
                $("html, body").animate({ scrollTop: 0 }, "fast");
            });
        };

        // aggiorna proiezioni esistenti
        this.updatePlays = function () {
            $rootScope.loadPlaysByDate();
        };

        // init select (direttiva che emette l'event alla fine del file)
        $scope.$on('selectRepeatEnd', function(scope, element, attrs){
            setTimeout(function () {
                $('select').material_select();
                console.log("SELECT INIZIALIZZATI");
            },50);
        });

        // imposta un messaggio
        this.setStatus = function (status) {
            ctrl.setStatusClass();
            ctrl.status = status;
        };

        // imposta il tipo di messaggio
        this.setStatusClass = function () {
            if(ctrl.error_message) {
                $('#plays_message').removeClass("green-text white-text");
                $('#plays_message').addClass("red-text");
            } else {
                $('#plays_message').removeClass("red-text white-text");
                $('#plays_message').addClass("green-text");
            }
        };

        this.loadFilms();
    }])
        /* controller per la visualizzazione delle proiezioni (admin) */
        .controller('AdminPlaysController', ['$rootScope', '$location', 'Plays', 'Rooms', 'Films', function ($rootScope, $location, Plays, Rooms, Films) {

            var ctrl = this;
            this.status = "";
            this.error_message = false;
            this.currentPlay = {};
            this.currentSelectedDate = -1;
            this.currentSelectedFilm = -1;
            this.currentSelectedPlay = -1;
            this.shared_obj = {};
            this.films = [];

            this.init = function () {
                if ($rootScope.isUserLogged == false) {
                    $rootScope.afterLogin = "userArea";
                    $location.path('/login');
                }
                ctrl.loading = true;
                ctrl.currentPlay = {};
                ctrl.loadFilms();
            };

            // carica i film disponibili
            this.loadFilms = function () {

                Films.query(function (data) {
                    ctrl.films = data;
                    console.log(ctrl.films);
                }, function (){
                    ctrl.error_message = true;
                    ctrl.setStatus("Errore durante il caricamento dei film");
                });
            };


            // recupera la sala relativa alla proiezione selezionata
            this.setCurrentPlay = function (indexDate, indexFilm, indexPlay) {

                ctrl.currentPlay = ctrl.playGenerator(indexDate, indexFilm, indexPlay);
                ctrl.currentSelectedDate = indexDate;
                ctrl.currentSelectedFilm = indexFilm;
                ctrl.currentSelectedPlay = indexPlay;

                Rooms.getRoomStatus(ctrl.currentPlay.pid)
                    .success(function (data) {
                        ctrl.shared_obj.editable = false;
                        ctrl.shared_obj.mapTheatre = data.seats;
                    })
                    .error(function (data, status) {
                        ctrl.shared_obj.editable = false;
                        ctrl.shared_obj.mapTheatre = [];
                        ctrl.error_message = true;
                        ctrl.setStatus("Errore durante il caricamento della mappa");
                        $("html, body").animate({ scrollTop: 0 }, "fast");
                    });
            };

            // elimina una proiezione
            this.deletePlay = function () {
                Plays.delete({id: ctrl.currentPlay.pid}).$promise.then(function () {
                    console.log("Play deletion success");
                    ctrl.updatePlays();
                    ctrl.close_delete_modal();
                    ctrl.error_message = false;
                    ctrl.setStatus("Proiezione cancellata con successo");
                    $("html, body").animate({ scrollTop: 0 }, "fast");
                }, function (response) {
                    if (response.status == 409) {
                        ctrl.error_message = true;
                        ctrl.setStatus("Impossibile cancellare la proiezione, alcuni posti sono già prenotati");
                    } else {
                        ctrl.error_message = true;
                        ctrl.setStatus("Si è verificato un errore, riprova");
                    }
                    ctrl.close_delete_modal();
                    $("html, body").animate({ scrollTop: 0 }, "fast");
                });
            };

            // apre modal conferma cancellazione
            this.open_delete_modal = function (indexDate, indexFilm, indexPlay) {
                ctrl.setCurrentPlay(indexDate, indexFilm, indexPlay);
                $('#modal_deleteAgree').openModal();
            };

            // chiude modal conferma cancellazione
            this.close_delete_modal = function () {
                $('#modal_deleteAgree').closeModal();
            };

            // genera un oggetto contenente tutti i dati relativi ad una proiezione a partire dai dati dell'array complesso delle proiezioni
            this.playGenerator = function (indexDate, indexFilm, indexPlay) {
                var tmpFilm = ctrl.cloneObject($rootScope.playsByDate[indexDate].films[indexFilm]);
                tmpFilm.pid = tmpFilm.plays[indexPlay].pid;
                tmpFilm.rid = tmpFilm.plays[indexPlay].rid;
                tmpFilm.free = tmpFilm.plays[indexPlay].free;
                tmpFilm.time = new Date(tmpFilm.plays[indexPlay].time);
                delete tmpFilm.date;
                delete tmpFilm.plays;
                return tmpFilm;
            };

            // copia un oggetto
            this.cloneObject = function (obj) {
                return (JSON.parse(JSON.stringify(obj)));
            };

            // aggiorna la lista di proiezioni esistenti
            this.updatePlays = function () {
                $rootScope.loadPlaysByDate();
            };

            // imposta un messaggio
            this.setStatus = function (status) {
                ctrl.setStatusClass();
                ctrl.status = status;
            };

            // imposta il tipo di messaggio
            this.setStatusClass = function () {
                if(ctrl.error_message) {
                    $('#plays_message').removeClass("green-text white-text");
                    $('#plays_message').addClass("red-text");
                } else {
                    $('#plays_message').removeClass("red-text white-text");
                    $('#plays_message').addClass("green-text");
                }
            };

            this.init();
        }]);
})();
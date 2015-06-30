(function () {
    'use strict';

    angular.module('pricesModule', ['constantsModule', 'ngResource'])
        .factory('Prices', ['BASE', '$resource', function (BASE, $resource) {
            return $resource(BASE + '/prices/:type', {type: '@type'}, {
                update: {
                    method: 'POST'
                }
            });
        }]);
})();
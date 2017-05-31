
'use strict';

angular.module('clinicApp.homepage', [])
    .controller('HomepageCtrl', function ($scope, $location)
    {
        $scope.go = function ( path )
        {
            $location.path( path );
        };
    });
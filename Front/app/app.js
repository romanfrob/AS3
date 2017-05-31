'use strict';

angular.module('clinicApp', [
    'ngRoute',
    'ngCookies',
    'clinicApp.homepage',
    'clinicApp.employee',
    'clinicApp.manager',
    'clinicApp.doctor',
    'clinicApp.login'
])
    .config(['$routeProvider', function($routeProvider)
    {
        $routeProvider
            .when('/', {
                templateUrl: 'Views/homepage.html',
                controller: 'HomepageCtrl'
            })
            .when('/manager', {
                templateUrl: 'Views/manager.html',
                controller: 'ManagerCtrl'
            })
            .when('/employee', {
                templateUrl: 'Views/employee.html',
                controller: 'EmployeeCtrl'
            })
            .when('/doctor', {
                templateUrl: 'Views/doctor.html',
                controller: 'DoctorCtrl'
            })
            .when('/login', {
                templateUrl: 'Views/login.html',
                controller: 'LoginCtrl'
            })
            .otherwise({redirectTo: '/'});
    }])
    .run(['$rootScope', '$location', '$cookieStore', '$http', function($rootScope, $location, $cookieStore, $http)
    {
        // keep user logged in after page refresh
        $rootScope.globals = $cookieStore.get('globals') || {};
        if ($rootScope.globals.currentUser)
        {
            $http.defaults.headers.common['Authorization'] = 'Basic ' + $rootScope.globals.currentUser.authdata;
        }

        $rootScope.$on('$locationChangeStart', function ()
        {
            //redirect to homepage if not logged in and trying to access a restricted page

            var restrictedPage = $.inArray($location.path(), ['/login', '/']) === -1;
            var loggedIn = $rootScope.globals.currentUser;
            if(restrictedPage && !loggedIn)
            {
                $location.path('/');
            }
            //redirect to homepage if employee tries to access manager page
            if(loggedIn && $rootScope.globals.currentUser.userType !== "manager" && $location.path() === '/manager')
            {
                $location.path('/');
            }
            //skip login page if already logged in
            if(loggedIn && $rootScope.globals.currentUser.userType === "employee" && $location.path() === '/login')
            {
                $location.path('/employee')
            }
            if(loggedIn && $rootScope.globals.currentUser.userType === "manager" && $location.path() === '/login')
            {
                $location.path('/manager')
            }
            if(loggedIn && $rootScope.globals.currentUser.userType === "doctor" && $location.path() === '/login')
            {
                $location.path('/doctor')
            }
        });
    }])
    .factory('requestFactory', function ($http)
    {
        var factory = {};

        //admin
        factory.getAllUsers = function () {
            return $http.get("http://localhost:8080/Clinic/api/user/all");
        };

        factory.createUser = function (user, config) {
            return $http.post("http://localhost:8080/Clinic/api/user/add", user, config);
        };

        factory.updateUser = function (user, config) {
            return $http.put("http://localhost:8080/Clinic/api/user/update", user, config);
        };

        factory.removeUser = function (user) {
            return $http.delete("http://localhost:8080/Clinic/api/user/remove?userID=" + user);
        };

        //doctor
        factory.checkOut = function (consult, config) {
            return $http.put("http://localhost:8080/Clinic/api/consult/check-out", consult, config);
        };

        factory.getDoctorConsults = function (doctorID) {
            return $http.get("http://localhost:8080/Clinic/api/consult/doctor?doctorID=" + doctorID);
        };

        factory.getPatientConsults = function (patientCNP) {
            return $http.get("http://localhost:8080/Clinic/api/consult/patient?cnp=" + patientCNP);
        };

        //secretary
        factory.getAllPatients = function () {
            return $http.get("http://localhost:8080/Clinic/api/patient/all");
        };

        factory.createPatient = function (patient, config) {
            return $http.post("http://localhost:8080/Clinic/api/patient/add", patient, config);
        };

        factory.updatePatient = function (patient, config) {
            return $http.put("http://localhost:8080/Clinic/api/patient/update", patient, config);
        };

        factory.getAllConsults = function () {
            return $http.get("http://localhost:8080/Clinic/api/consult/all");
        };

        factory.createConsult = function (consult, config) {
            return $http.post("http://localhost:8080/Clinic/api/consult/add", consult, config);
        };

        factory.updateConsult = function (consult, config) {
            return $http.put("http://localhost:8080/Clinic/api/consult/update", consult, config);
        };

        factory.checkIn = function (consult, config) {
            return $http.put("http://localhost:8080/Clinic/api/consult/check-in", consult, config);
        };

        factory.checkOut = function (consult, config) {
            return $http.put("http://localhost:8080/Clinic/api/consult/check-out", consult, config);
        };

        factory.getAllDoctors = function () {
            return $http.get("http://localhost:8080/Clinic/api/doctor/all");
        };

        factory.getDoctorsForStatus = function (status) {
            return $http.get("http://localhost:8080/Clinic/api/doctor/status?status=" + status);
        };

        factory.checkAvailabilityOfDoctor = function (time, doctorID) {
            return $http.get("http://localhost:8080/Clinic/api/doctor/check?time=" + time + "&doctorID=" + doctorID);
        };

        factory.getAvailableDoctors = function (moment) {
            return $http.get("http://localhost:8080/Clinic/api/doctor/time?moment=" + moment);
        };

        factory.notificationForDoctor = function (doctorID) {
            return $http.get("http://localhost:8080/Clinic/api/doctor/notification?doctorID=" + doctorID);
        };

        return factory;
    });


angular.module('clinicApp.manager', [])
    .controller('ManagerCtrl', function ($scope, $q, requestFactory, $location, AuthenticationService)
    {
        $scope.users = [];
        $scope.selectedUser = {};
        $scope.inputType = "password";
        $scope.doctorName = "";
        var config = {headers: {'Content-Type': 'application/json'}};
        
        $scope.getUsers = function () {
            requestFactory.getAllUsers()
                .then(
                    function (response) {
                        $scope.users = response.data;
                    },
                    function(errResponse, status){
                        alert("Error while retrieving users: " + status);
                        return $q.reject(errResponse);
                    }
                )
        };

        $scope.getUsers();

        $scope.showUser = function (user) {
            $scope.selectedUser = user;
        };

        $scope.clearForm = function () {
            $scope.selectedUser = {};
        };

        $scope.convertToDate = function (timestamp) {
            return moment(timestamp).format("DD-MM-YYYY");
        };
        

        $scope.insertUser = function () {
            var user = {};
            user['username'] = $scope.selectedUser.username;
            user['password'] = $scope.selectedUser.password;
            user['type'] = $scope.selectedUser.type;
            user['doctorName'] = $scope.doctorName;

            requestFactory.createUser(user, config)
                .then(
                    function () {
                        $scope.getUsers();
                        $scope.clearForm();
                    },
                    function ()
                    {
                        alert("Duplicate username: " + status);
                    });
        };

        $scope.updateUser = function () {
            var user = {};
            user['userID'] = $scope.selectedUser.userId;
            user['username'] = $scope.selectedUser.username;
            user['password'] = $scope.selectedUser.password;
            user['type'] = $scope.selectedUser.type;
            user['doctorName'] = $scope.doctorName;

            requestFactory.updateUser(user, config)
                .then(
                    function () {
                        $scope.getUsers();
                        $scope.clearForm();
                    },
                    function ()
                    {
                        alert("Duplicate username: " + status);
                    });
        };

        $scope.deleteUser = function () {
            requestFactory.removeUser($scope.selectedUser.userId)
                .then(
                    function () {
                        $scope.getUsers();
                        $scope.clearForm();
                    }
                )
        };

        $scope.hideShowPassword = function(){
            if ($scope.inputType == 'password')
                $scope.inputType = 'text';
            else
                $scope.inputType = 'password';
        };

        $scope.logout = function () {
            $location.path('/');
            AuthenticationService.ClearCredentials();
        }
    });
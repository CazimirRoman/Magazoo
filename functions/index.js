const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.modifyShopProperty = functions.database
    .ref('/Reports/{pushId}')
    .onWrite((change, context) => {

        var reportedShopId = change.after.child("shopId").val();
        var typeOfReport = change.after.child("regards").val();
        var valueOfReport = change.after.child("howIsIt").val();
        //get all reports matching the reported shop id
        return admin.database().ref('/Reports')
            .orderByChild('shopId').equalTo(reportedShopId)
            .once('value').then(snapshot => {

                var numberOfLocationReports = 0;
                var numberOfTicketReports = 0;
                var numberOfNonstopReports = 0;
                var numberOfPosReports = 0;
                //get reported issue (location, pos, nonstop, tickets)
                snapshot.forEach(function(childSnapshot) {

                    if (childSnapshot.child("regards").val() === "location" && childSnapshot.child("resolved").val() === false) {
                        numberOfLocationReports++;
                        if (numberOfLocationReports >= 3) {

                            admin.database().ref('/Reports')
                                .orderByChild('shopId').equalTo(reportedShopId)
                                .once('value').then(snapshot => {

                                    snapshot.forEach(function(childSnapshot) {
                                        childSnapshot.ref.update({
                                            resolved: true
                                        })
                                    });

                                });

                            console.log("Location report threshold reached!")
                            var ref = admin.database().ref('/Stores');
                            ref.orderByKey().equalTo(reportedShopId).once("value", function(snapshot) {
                                var updates = {};
                                snapshot.forEach(function(child) {
                                    updates[child.key] = null;
                                });
                                ref.update(updates);
                                console.log("Deleted shop with id: " + reportedShopId);
                            });
                        };
                    } else if (childSnapshot.child("regards").val() === "tickets" && childSnapshot.child("resolved").val() === false) {
                        numberOfTicketReports++;
                        if (numberOfTicketReports >= 3) {
                            //set report on resolved(true) so it is not considered next time when searching
                            admin.database().ref('/Reports')
                                .orderByChild('shopId').equalTo(reportedShopId)
                                .once('value').then(snapshot => {

                                    snapshot.forEach(function(childSnapshot) {
                                        childSnapshot.ref.update({
                                            resolved: true
                                        })
                                    });

                                });

                            console.log("Ticket report threshold reached!")
                            var ref = admin.database().ref('/Stores');
                            var query = ref.orderByKey().equalTo(reportedShopId);
                            query.once("child_added", function(snapshot) {
                                snapshot.ref.update({
                                    tickets: valueOfReport
                                })
                            });
                            console.log("Updated shop with ticket value set to: " + valueOfReport);
                        };

                    } else if (childSnapshot.child("regards").val() === "nonstop" && childSnapshot.child("resolved").val() === false) {
                        numberOfNonstopReports++;
                        if (numberOfNonstopReports >= 3) {
                            //set report on resolved(true) so it is not considered next time when searching
                            admin.database().ref('/Reports')
                                .orderByChild('shopId').equalTo(reportedShopId)
                                .once('value').then(snapshot => {

                                    snapshot.forEach(function(childSnapshot) {
                                        childSnapshot.ref.update({
                                            resolved: true
                                        })
                                    });

                                });
                            console.log("Nonstop report threshold reached!")
                            var ref = admin.database().ref('/Stores');
                            var query = ref.orderByKey().equalTo(reportedShopId);
                            query.once("child_added", function(snapshot) {
                                snapshot.ref.update({
                                    nonstop: valueOfReport
                                })
                            });
                            console.log("Updated shop with nonstop value set to: " + valueOfReport);
                        };

                    } else if (childSnapshot.child("regards").val() === "pos" && childSnapshot.child("resolved").val() === false) {
                        numberOfPosReports++;
                        if (numberOfPosReports >= 3) {
                            //set report on resolved(true) so it is not considered next time when searching
                            admin.database().ref('/Reports')
                                .orderByChild('shopId').equalTo(reportedShopId)
                                .once('value').then(snapshot => {

                                    snapshot.forEach(function(childSnapshot) {
                                        childSnapshot.ref.update({
                                            resolved: true
                                        })
                                    });

                                });
                            console.log("Pos report threshold reached!")
                            var ref = admin.database().ref('/Stores');
                            var query = ref.orderByKey().equalTo(reportedShopId);
                            query.once("child_added", function(snapshot) {
                                snapshot.ref.update({
                                    pos: valueOfReport
                                })
                            });
                            console.log("Updated shop with pos value set to: " + valueOfReport);
                        };
                    }
                });
            });
    });
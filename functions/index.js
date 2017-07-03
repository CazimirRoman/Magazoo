const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.deleteReportedShop = functions.database
  .ref('/Reports/{pushId}')
  .onWrite(event => {
    
    var reportedShopId = event.data.child("shopId").val();
    var typeOfReport = event.data.child("regards").val();
    var valueOfReport = event.data.child("howisit").val();
    var numberOfLocationReports = 0;
    var numberOfTicketReports = 0;
    var numberOfNonstopReports = 0;
    var numberOfPosReports = 0;

    return admin.database().ref('/Reports')
      .orderByChild('shopId').equalTo(reportedShopId)
      .once('value').then(snapshot => {
        snapshot.forEach(function(childSnapshot){
            if(childSnapshot.child("regards").val() === "location"){
               numberOfLocationReports++;
                if(numberOfLocationReports >= 3){
                    console.log("Location report threshold reached!")
                    var ref = admin.database().ref('/Stores');
                    ref.orderByKey().equalTo(reportedShopId).once("value", function(snapshot){
                     var updates = {};
                     snapshot.forEach(function(child){
                         updates[child.key] = null;
                     });
                     ref.update(updates);
                     console.log("Deleted shop with id: " + reportedShopId);
                     return false;
                });
               };
        } else if(childSnapshot.child("regards").val() === "tickets"){
            numberOfTicketReports++;
            if(numberOfTicketReports >= 3){
                console.log("Ticket report threshold reached!")
                var ref = admin.database().ref('/Stores');
                var query = ref.orderByKey().equalTo(reportedShopId);
                query.once("child_added", function(snapshot) {
                    snapshot.ref.update({ tickets: valueOfReport })
                    });
                    console.log("Updated shop with ticket value set to: " + valueOfReport);
                    return false;
                };
                
            } else if(childSnapshot.child("regards").val() === "nonstop"){
            numberOfNonstopReports++;
            if(numberOfNonstopReports >= 3){
                console.log("Nonstop report threshold reached!")
                var ref = admin.database().ref('/Stores');
                var query = ref.orderByKey().equalTo(reportedShopId);
                query.once("child_added", function(snapshot) {
                    snapshot.ref.update({ nonstop: valueOfReport })
                    });
                    console.log("Updated shop with nonstop value set to: " + valueOfReport);
                    return false;
                };
                
            }else if(childSnapshot.child("regards").val() === "pos"){
            numberOfPosReports++;
            if(numberOfPosReports >= 3){
                console.log("Pos report threshold reached!")
                var ref = admin.database().ref('/Stores');
                var query = ref.orderByKey().equalTo(reportedShopId);
                query.once("child_added", function(snapshot) {
                    snapshot.ref.update({ pos: valueOfReport })
                    });
                    console.log("Updated shop with pos value set to: " + valueOfReport);
                    return false;
                };
                
            }
        });
  });
});
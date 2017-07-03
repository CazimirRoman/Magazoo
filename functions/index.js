const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
// Take the text parameter passed to this HTTP endpoint and insert it into the
// Realtime Database under the path /messages/:pushId/original
exports.addMessage = functions.https.onRequest((req, res) => {
    // Grab the text parameter.
    const original = req.query.int;
    // Push the new message into the Realtime Database using the Firebase Admin SDK.
    admin.database().ref('/messages').push({
        number: original
    }).then(snapshot => {
        // Redirect with 303 SEE OTHER to the URL of the pushed object in the Firebase console.
        res.redirect(303, snapshot.ref);
    });
});

exports.deleteReportedShop = functions.database
  .ref('/Reports/{pushId}')
  .onWrite(event => {
    
    var reportedShopId = event.data.child("shopId").val();
    var typeOfReport = event.data.child("regards").val();
    var numberOfLocationReports = 0;

    return admin.database().ref('/Reports')
      .orderByChild('shopId').equalTo(reportedShopId)
      .once('value').then(snapshot => {
        snapshot.forEach(function(childSnapshot){
            
            if(childSnapshot.child("regards").val() === "location"){
               numberOfLocationReports++;
                if(numberOfLocationReports >= 3){
                    console.log("Avem mai mult de 3 raportari de locatie!")
                    var ref = admin.database().ref('/Stores');
                    ref.orderByKey().equalTo(reportedShopId).once("value", function(snapshot){
                     var updates = {};
                     snapshot.forEach(function(child){
                          updates[child.key] = null;
                         console.log(child);
                         console.log("a gasit un child");
                     });
                     ref.update(updates);
                     console.log("Deleted shop with id: " + reportedShopId);
                     return false;
                });
               }
        
        };
      });
  });
});
var router = require("express").Router();
var fs = require("fs");
    util = require("util");

var mime = require("mime");

const fileUpload = require('express-fileupload');


/**
 * ::::::::: USER :::::::::
 * 
 *      UID, 
 *      email,
 *      nickname, 
 *      picture,
 *      detail
 */


router.route('/info').post(function (req, res) {


    let userModel = req.app.get("database").user;
    let uid = req.body.uid;
    let email = req.body.email;
    let nickname = req.body.nickname;
    let picture = "profiles/" + uid + ".jpg";
    let age = req.body.age;
    let detail = req.body.detail;

    let sampleFile = req.files.sampleFile;
    sampleFile.mv(picture);


    new userModel({
        "uid": uid,
        "email": email,
        "nickname": nickname,
        "picture": picture,
        "age": age,
        "detail": detail
    }).save(function (err, user) {
        if (err) {
            res.status(400).end();
        }
        else {
            res.status(201).end();
        }
    });

});
router.route('/info').put(function (req, res) {
    if(req.files) {
        let sampleFile = req.files.sampleFile;
        sampleFile.mv("profiles/" + req.body.uid + ".jpg");
    }
    let userModel = req.app.get("database").user;
    userModel.update({ "uid": req.body.uid }, {
        "$set": req.body,
    }, {
            multi: true
        },(err, output) => {
            if(err) {
                res.status(400).end();
            }
            else {
                res.status(200).end();
            }
        }
    )
});
router.route('/info/:userId').get(function (req, res) {
    let userModel = req.app.get("database").user;
    userModel.findOne({ "uid": req.params.userId }, function (err, doc) {
        if (err) {
            res.status(400).end();
        }
        else {
            var src = doc.picture;
            var data = fs.readFileSync(src).toString("base64");
            //var dataUri = util.format("data:%s;base64,%s", mime.lookup(src), data);
            var dataUri = util.format("%s", data);
            doc.picture = dataUri;
            res.status(200).json(doc);
        }
    });
});


module.exports = router;
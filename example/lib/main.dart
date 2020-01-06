import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:dio/dio.dart';
import 'package:media_file_saver/media_file_saver.dart';
import 'dart:typed_data';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'media_file_helper_example',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return _MyHomePageState();
  }
}

class _MyHomePageState extends State<MyHomePage> {
  final _imgUrl =
      "https://upload.wikimedia.org/wikipedia/commons/1/17/Google-flutter-logo.png";

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("MediaFileSaver Example"),
      ),
      body: Center(
        child: Column(
          children: <Widget>[
            Image.network(
              _imgUrl,
              height: 200,
              width: 200,
            ),
            Container(
              padding: EdgeInsets.only(top: 15),
              child: RaisedButton(
                onPressed: _saveGoogleImage,
                child: Text("Download Image"),
              ),
              width: 200,
              height: 56,
            ),
          ],
        ),
      ),
    );
  }

  _saveGoogleImage() async {
    var response = await Dio().get(
      _imgUrl,
      options: Options(responseType: ResponseType.bytes),
    );
    final result = await MediaFileSaver.saveImage(
      Uint8List.fromList(response.data),
    );
    print("Saved path: $result");
  }
}

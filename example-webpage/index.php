<!doctype html>
<?php

	/**
	 * Replace the entity ID with your own. Also use the same authnContextClass 
	 * that you defined in your IDP for the second factor method. These are only
	 * example values, which won't work in your environment
	 */

	const IDPENTITYID_CONST = 'https://idptest.scc.kit.edu/idp/shibboleth';
	const IDP2FAMETH_CONST = 'https://idp.scc.kit.edu/authn/linotp';

	$idp2faMeth = IDP2FAMETH_CONST;
	$idpEntityId = IDPENTITYID_CONST;

?>
<html>
  <head>
    <meta charset="utf-8">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta http-equiv="cleartype" content="on" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />

    <title>Zwei Faktor Testseite</title>
    <link href='https://fonts.googleapis.com/css?family=Roboto' rel='stylesheet' type='text/css'>
    <link href='https://fonts.googleapis.com/icon?family=Material+Icons' rel='stylesheet' type='text/css'>
    
    <style>

html {
  padding: 0;
  margin: 0;
}
body {
  font-family: 'Roboto', sans-serif;
}
h1 {
  font-size: 1.2em;
  font-weight: bold;
  margin-left: 1.4em;
}
div { padding: 0.2em; }
div#container {
  display: flex;
}
div#fchild {
  border: 1px solid gray;
  margin: 1.0em;
  padding: 0.4em;
}
    </style>
  </head>
<body>

<h1>Zwei Faktor Testseite</h1>

<div id="container">
<?php

$replace_meth = array( 
"urn:oasis:names:tc:SAML:2.0:ac:classes:Kerberos" => "Kerberos", 
"urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport" => "Username/Password",
"urn:oasis:names:tc:SAML:2.0:ac:classes:X509" => "X509 Zertifikat",
IDP2FAMETH_CONST => "Zweiter Faktor"
); 

if (! isset($_SERVER['eppn'])) {
  print "<div id='fchild' style='background-color: #ffcccc;'>";
  print "<div style='text-align: center;'><span class='material-icons' style='font-size: 5em;'>remove_circle_outline</span></div>";
  print "<div>Nicht angemeldet</div>";
  print "<div><a href='/Shibboleth.sso/Login?entityID=$idpEntityId&target=/secure-test/'>Jetzt anmelden</a></div>";
  print "</div>";
}
else {
  print "<div id='fchild' style='background-color: #ccffcc;'>";
  print "<div style='text-align: center;'><span class='material-icons' style='font-size: 5em;'>perm_identity</span></div>";
  print "<div>Angemeldet als: ".$_SERVER['eppn']."</div>";
  print "<div>Anmeldemethode: ".str_replace(array_keys($replace_meth), array_values($replace_meth), $_SERVER['Shib-AuthnContext-Class'])."</div>";
  print "</div>";

  if ($_SERVER['Shib-AuthnContext-Class'] != $idp2faMeth) {
    print "<div id='fchild' style='background-color: #ffcccc;'>";
    print "<div style='text-align: center;'><span class='material-icons' style='font-size: 5em;'>security</span></div>";
    print "<div>Kein zweiter Faktor</div>";
    print "<div><a href='/Shibboleth.sso/Login?entityID=$idpEntityId&authnContextClassRef=$idp2faMeth&target=/secure-test/'>Mit zweitem Faktor bestätigen</a></div>";
    print "</div>";
  }
  else {
    print "<div id='fchild' style='background-color: #ccffcc;'>";
    print "<div style='text-align: center;'><span class='material-icons' style='font-size: 5em;'>verified_user</span></div>";
    print "<div>Mit zweitem Faktor besätigt</div>";
    print "<div><a href='/Shibboleth.sso/Login?entityID=$idpEntityId&authnContextClassRef=$idp2faMeth&forceAuthn=true&target=/secure-test/'>Erneut mit zweitem Faktor bestätigen</a></div>";
    print "</div>";
  }
}
?>
</div>

</body>
</html>


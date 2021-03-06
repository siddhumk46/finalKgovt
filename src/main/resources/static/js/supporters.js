let headerhtml= '';
headerhtml += '<span id="modalHTml"></span>';
headerhtml += '<span id="waringId"></span>';
headerhtml +=' <div class="row">';
headerhtml += ' <div class="col-md-12 d-none d-sm-none d-md-block headerDiv">';
headerhtml += '   <img src="/img/logo1.jpg">';
headerhtml += '   <p class="font-weight-normal" style="font-size:7ch; text-align:center;"> Karnataka State co-operative Federation Ltd.,</p>';
headerhtml += '   <p style="text-align:center; font-size: medium; color: rgb(121, 171, 236);"> Diploma in Co-operative Management (Distance Education)</p>';
headerhtml += '   </div>';
headerhtml += ' </div>';

headerhtml += '		<nav class="navbar navbar-expand-lg navbar-dark bg-warning orange">';
headerhtml += '			<button class="navbar-toggler" type="button" data-toggle="collapse" ';
headerhtml += '				data-target="#navbarNav" aria-controls="navbarNav" ';
headerhtml += '				aria-expanded="false" aria-label="Toggle navigation"> ';
headerhtml += '				<span class="navbar-toggler-icon"></span>';
headerhtml += '			</button>';
headerhtml += '			<div class="collapse navbar-collapse" id="navbarNav">';
headerhtml += '				<ul class="navbar-nav">';
headerhtml += '					<li class="nav-item"><a class="nav-link" href="/">Home';
headerhtml += '							Page</a></li>';
headerhtml += '					<li class="nav-item"><a class="nav-link" data-toggle="modal" data-target="#modalAbandonedCart" href="/new">Apply';
headerhtml += '							Online</a></li>';
headerhtml += '					<li class="nav-item"><a class="nav-link" href="/status">Status</a></li>';
headerhtml += '					<li class="nav-item"><a class="nav-link" href="/contact">Contact';
headerhtml += '							Us</a></li>';
headerhtml += '				</ul>';
headerhtml += '			</div>';
headerhtml += '		</nav>';

let footerhtml= '';
footerhtml += '	<hr class="mt-2 mb-3"/> ';
footerhtml += '  <div class="bs-example">';
footerhtml += '    <div class="bg-warning d-flex justify-content-between">';
footerhtml += '        <div>&nbsp&nbspOwned And Maintained By: Karnataka State co-operative Federation Ltd., </div>';
footerhtml += '        <div>Designed and Deveploed By: www.apito.in&nbsp&nbsp</div>';
footerhtml += '    </div>';
footerhtml += '</div>';

let righthtml= '<ul><li>Phone no: +91 8892075276,+91 8660193339</li><li style="word-break: break-all">E-mail: apitotechnologies@gmail.com</li></ul>';

let lefthtml= '';
lefthtml += '<ul>';
lefthtml += '	<li><a href="./resource/Noc or Deputing Letter.pdf" rel="nofollow">';
lefthtml += '			NOC template</a></li>';
lefthtml += '	<li><a href="./resource/Service Certificate.pdf" rel="nofollow">';
lefthtml += '			Service Certificate template</a></li>';
lefthtml += '	<li><a href="/offline">Offline Application Form</a></li>';
lefthtml += '</ul>';

let warningHtml= '';
warningHtml += '<div class="modal fade right" id="modalAbandonedCart" tabindex="-1" ';
warningHtml += '		role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" ';
warningHtml += '		data-backdrop="false"> ';
warningHtml += '		<div ';
warningHtml += '			class="modal-dialog modal-side modal-bottom-right modal-notify modal-info" ';
warningHtml += '			role="document"> ';
warningHtml += '			<div class="modal-content"> ';
warningHtml += '				<div class="modal-header"> ';
warningHtml += '					<p class="heading" style="padding-left: 10%;"> ';
warningHtml += '					<h2>INFO!!!</h2>';
warningHtml += '					</p>';
warningHtml += '					<button type="button" class="close" data-dismiss="modal"';
warningHtml += '						aria-label="Close">';
warningHtml += '						<span aria-hidden="true" class="white-text">&times;</span>';
warningHtml += '					</button>';
warningHtml += '				</div>';
warningHtml += '				<div class="modal-body">';
warningHtml += '					<div class="row">';
warningHtml += '						<div class="col-3">';
warningHtml += '							<p></p>';
warningHtml += '							<p class="text-center">';
warningHtml += '								<i class="fas fa-shopping-cart fa-4x"></i>';
warningHtml += '							</p>';
warningHtml += '						</div>';
warningHtml += '						<div>';
warningHtml += '							<p style="padding-left: 10%;"> Please keep the following documents';
warningHtml += '								scanned in required format and please keep the';
warningHtml += '								restricted file to 1mb</p>';
warningHtml += '							   <ul>';
warningHtml += '					              <li>NOC</li>';
warningHtml += '					              <li>Photo Copy</li>';
warningHtml += '					              <li>Signature</li>';
warningHtml += '					              <li>Address Proof</li>';
warningHtml += '					              <li>Employee Certificate</li>';
warningHtml += '					            </ul>	';
warningHtml += '						</div>';
warningHtml += '					</div>';
warningHtml += '				</div>';
warningHtml += '				<div class="modal-footer justify-content-center">';
warningHtml += '					<a type="button" class="btn btn-info" href="/new">Proceed</a>';
warningHtml += '					<a type="button" class="btn btn-outline-info waves-effect"';
warningHtml += '						data-dismiss="modal" href="#">Cancel</a>';
warningHtml += '				</div>';
warningHtml += '			</div>';
warningHtml += '		</div>';
warningHtml += '	</div>';

$('#headerId').html(headerhtml);
$('#footerId').html(footerhtml);
$('#leftId').html(lefthtml);
$('#rightId').html(righthtml);
$('#waringId').html(warningHtml);

function adminLoginCall(){
	window.location.href='/admin/login';
}

function candidateLoginCall() {
	window.location.href='/user/login';
}
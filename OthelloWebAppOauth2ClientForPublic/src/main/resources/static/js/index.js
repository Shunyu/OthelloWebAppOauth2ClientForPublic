/**
 * 
 */
jQuery(function($) {
	$('.bg-slider').bgSwitcher({
	images: ["../images/ChichibugahamaBeach.jpg","../images/Kiro-sanObservatory.jpeg","../images/MarugameCastle.jpg","../images/Mimido.jpeg","../images/MtIino.jpg","../images/MtShiude.jpg","../images/ShikokuKarst.jpeg","../images/ShimonadaStation.jpeg","../images/GreatSetoBridge.jpg","../images/MatsuyamaCastle.jpg","../images/Nanaore.jpeg","../images/Yashima.jpg"],
	interval: 5000,
	loop: true,
	shuffle: true,
	effect: "fade",
	duration: 500,
	easing: "swing"
	});
});
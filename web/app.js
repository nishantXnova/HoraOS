// WispOS site interactions — quiet, fast, no dependencies.
(function () {
  "use strict";

  /* Nav blur */
  var nav = document.getElementById("nav");
  var onScroll = function () {
    nav.classList.toggle("scrolled", window.scrollY > 12);
  };
  window.addEventListener("scroll", onScroll, { passive: true });
  onScroll();

  /* Mobile menu */
  var menuBtn = document.getElementById("menuBtn");
  var mobileMenu = document.getElementById("mobileMenu");
  menuBtn.addEventListener("click", function () {
    mobileMenu.classList.toggle("open");
  });
  mobileMenu.querySelectorAll("a").forEach(function (a) {
    a.addEventListener("click", function () { mobileMenu.classList.remove("open"); });
  });

  /* Reveal on scroll */
  var revealEls = document.querySelectorAll(".reveal");
  if ("IntersectionObserver" in window) {
    var io = new IntersectionObserver(function (entries) {
      entries.forEach(function (e) {
        if (e.isIntersecting) { e.target.classList.add("is-in"); io.unobserve(e.target); }
      });
    }, { threshold: 0.12, rootMargin: "0px 0px -6% 0px" });
    revealEls.forEach(function (el) { io.observe(el); });
  } else {
    revealEls.forEach(function (el) { el.classList.add("is-in"); });
  }

  /* Live clocks — hero + mini share your time */
  function pad(n) { return (n < 10 ? "0" : "") + n; }
  function tick() {
    var d = new Date();
    var h = d.getHours(), m = d.getMinutes();
    var fh = document.getElementById("faceHours");
    var fm = document.getElementById("faceMins");
    if (fh) fh.textContent = pad(h);
    if (fm) fm.textContent = pad(m);
    var mh = document.getElementById("miniH");
    var mm = document.getElementById("miniM");
    if (mh) mh.textContent = pad(h);
    if (mm) mm.textContent = pad(m);

    var days = ["SUN","MON","TUE","WED","THU","FRI","SAT"];
    var months = ["JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"];
    var fd = document.getElementById("faceDate");
    if (fd) fd.textContent = days[d.getDay()] + " " + d.getDate();
    var md = document.getElementById("miniDate");
    if (md) md.textContent = days[d.getDay()] + " " + d.getDate() + " " + months[d.getMonth()];

    var hr = d.getHours();
    var greet = hr < 5 ? "Good night" : hr < 12 ? "Good morning" : hr < 18 ? "Good afternoon" : "Good evening";
    var mg = document.getElementById("miniGreet");
    var facesub = document.querySelector(".face-sub");
    if (mg) mg.innerHTML = greet + " · <span id='miniCount'>14</span> apps";
    if (facesub) facesub.textContent = greet;
  }
  tick();
  setInterval(tick, 5000);

  /* Fake battery drift, just for charm (92 -> 91 -> 92 ...) */
  var batt = 92;
  setInterval(function () {
    batt = batt <= 89 ? 93 : batt - 1;
    var fb = document.getElementById("faceBatt");
    if (fb) fb.textContent = batt + "%";
  }, 20000);

  /* Experience tabs */
  var copy = {
    face: {
      title: "Glanceable, even in sunlight.",
      text: "Big numerals. Today’s date. Battery and how many apps are waiting. It ticks every second because a watch should feel alive.",
      list: ["Live clock, date and battery", "Round-display tuned, crisp at arm’s length", "Nothing blinking for your attention"],
      hint: "←  You’re on the face. Swipe in your mind.  →"
    },
    controls: {
      title: "Controls, one swipe away.",
      text: "Wi-Fi, Bluetooth, brightness, battery — the four things you actually reach for. Tap one and you’re in the real system setting. No maze.",
      list: ["Four tiles, zero digging", "Each tile opens the real setting", "Readable with wet fingers, gloves, hurry"],
      hint: "←  Swipe left for controls.  →"
    },
    library: {
      title: "Every app, where you expect it.",
      text: "Your Library lists everything installed — with real names and icons, sorted neatly. Tap to open. That’s it. New installs simply appear.",
      list: ["Real apps, real icons, A–Z", "New installs appear automatically", "A tap launches — no long-press magic"],
      hint: "←  Swipe right for your Library.  →"
    }
  };
  var tabs = document.querySelectorAll(".exp-tab");
  var views = document.querySelectorAll(".mini-view");
  var expTitle = document.getElementById("expTitle");
  var expText = document.getElementById("expText");
  var expList = document.getElementById("expList");
  var miniHint = document.getElementById("miniHint");

  function selectTab(key) {
    tabs.forEach(function (t) { t.classList.toggle("is-active", t.dataset.tab === key); });
    views.forEach(function (v) { v.classList.toggle("is-visible", v.dataset.view === key); });
    var c = copy[key];
    if (!c) return;
    expTitle.style.opacity = 0; expText.style.opacity = 0;
    setTimeout(function () {
      expTitle.textContent = c.title;
      expText.textContent = c.text;
      expList.innerHTML = c.list.map(function (li) { return "<li>" + li + "</li>"; }).join("");
      miniHint.textContent = c.hint;
      expTitle.style.opacity = 1; expText.style.opacity = 1;
    }, 160);
  }
  expTitle.style.transition = "opacity .18s"; expText.style.transition = "opacity .18s";
  tabs.forEach(function (t) {
    t.addEventListener("click", function () { selectTab(t.dataset.tab); });
  });

  /* Subtle hero tilt */
  var stage = document.getElementById("heroStage");
  var watch = document.getElementById("watchTilt");
  if (stage && watch && window.matchMedia("(pointer:fine)").matches) {
    stage.addEventListener("mousemove", function (e) {
      var r = stage.getBoundingClientRect();
      var x = (e.clientX - r.left) / r.width - 0.5;
      var y = (e.clientY - r.top) / r.height - 0.5;
      watch.style.transform = "rotateY(" + (x * 12) + "deg) rotateX(" + (-y * 10) + "deg)";
    });
    stage.addEventListener("mouseleave", function () {
      watch.style.transform = "rotateY(0deg) rotateX(0deg)";
    });
  }
})();

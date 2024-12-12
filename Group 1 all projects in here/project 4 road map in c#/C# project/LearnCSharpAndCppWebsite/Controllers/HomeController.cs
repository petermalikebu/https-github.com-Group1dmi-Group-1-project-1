using System.Diagnostics; // Ensure no whitespace before `using` directives
using Microsoft.AspNetCore.Mvc;
using LearnCSharpAndCppWebsite.Models; // Ensure this namespace matches your project

namespace LearnCSharpAndCppWebsite.Controllers // Ensure correct spelling and casing
{
    public class HomeController : Controller
    {
        private readonly ILogger<HomeController> _logger;

        public HomeController(ILogger<HomeController> logger)
        {
            _logger = logger;
        }

        public IActionResult Index()
        {
            ViewData["Title"] = "Learn C# and C++ Website"; // Set the title here
            return View();
        }

        public IActionResult Privacy()
        {
            ViewData["Title"] = "Privacy"; // Optionally set a title for the Privacy page
            return View();
        }

        // Route for C# roadmap
        [HttpGet("roadmap-csharp")]
        public IActionResult RoadmapCSharp()
        {
            ViewData["Title"] = "C# Roadmap"; // Set title for C# roadmap
            return View("RoadmapCSharp");
        }

        // Route for C++ roadmap
        [HttpGet("roadmap-cpp")]
        public IActionResult RoadmapCpp()
        {
            ViewData["Title"] = "C++ Roadmap"; // Set title for C++ roadmap
            return View("RoadmapCpp");
        }

        [ResponseCache(Duration = 0, Location = ResponseCacheLocation.None, NoStore = true)]
        public IActionResult Error()
        {
            return View(new ErrorViewModel { RequestId = Activity.Current?.Id ?? HttpContext.TraceIdentifier });
        }

        public IActionResult BuildThisSite()
        {
            ViewData["Title"] = "Build This Site";
            return View();
        }

    }
}
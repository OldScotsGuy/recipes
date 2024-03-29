package org.nickharle.recipeapp.controllers;

import lombok.extern.slf4j.Slf4j;
import org.nickharle.recipeapp.commands.RecipeCommand;
import org.nickharle.recipeapp.exceptions.NotFoundException;
import org.nickharle.recipeapp.services.RecipeService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Slf4j
@Controller
public class RecipeController {

    private static final String RECIPE_RECIPEFORM_URL = "/recipe/recipeform";
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    // Take id out of URL i.e GET equivalent
    @GetMapping("/recipe/{id}/show")
    // Pass id into method
    public String showByID(@PathVariable String id, Model model) {

        model.addAttribute("recipe", recipeService.findById(new Long(id)));
        return "recipe/show";
    }

    @GetMapping("/recipe/new")
    public String newRecipe(Model model) {
        model.addAttribute("recipe", new RecipeCommand());
        return "recipe/recipeform";
    }

    @GetMapping("/recipe/{id}/update")
    public String updateRecipe(@PathVariable String id, Model model) {
        model.addAttribute("recipe", recipeService.findCommandById(Long.valueOf(id)));
        return RECIPE_RECIPEFORM_URL;
    }

    @GetMapping("/recipe/{id}/delete")
    public String deleteRecipe(@PathVariable String id) {
        log.debug("Deleting id:" + id);
        recipeService.deleteById(Long.valueOf(id));
        return "redirect:/";
    }


    // @RequestMapping(name="recipe", method = RequestMethod.POST) --- Old way
    @PostMapping("recipe")
    // @ModelAttribute tells Spring to bind the form parameters to the RecipeCommand object
    // @ModelAttribute("recipe") => bind the command object to the Model attribute "recipe" for the recipe form / show recipe view
    // BindingResult object contains the results of the @Valid test
    public String saveOrUpdate(@Valid @ModelAttribute("recipe") RecipeCommand command, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(objectError -> {
                log.debug(objectError.toString());
            });
            return RECIPE_RECIPEFORM_URL;
        }

        RecipeCommand savedCommand = recipeService.saveRecipeCommand(command);

        return "redirect:/recipe/"+ savedCommand.getId() +"/show";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleNotFound(Exception exception){

        log.error("Handling not found exception");
        log.error(exception.getMessage());

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("404error");
        modelAndView.addObject("exception", exception);


        return modelAndView;
    }
}

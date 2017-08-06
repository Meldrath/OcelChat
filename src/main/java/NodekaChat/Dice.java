package NodekaChat;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Random;

/**
 *
 * @author Isaac
 */
public class Dice {
    
    protected Random dice;
    
    protected Dice()
    {
        this.dice = new Random();
    }
    
    protected Random getDice()
    {
        return this.dice;
    }
    
    protected int rollDiceWithSides(int i)
    {
        return dice.nextInt(i) + 1;
    }
}

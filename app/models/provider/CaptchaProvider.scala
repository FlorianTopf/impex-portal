package models.provider

import play.api.Play.current
import play.api.Logger
import java.util.Properties
import net.tanesha.recaptcha.ReCaptchaFactory
import net.tanesha.recaptcha.ReCaptchaImpl
import net.tanesha.recaptcha.ReCaptchaResponse


object CaptchaProvider {
  def privateKey(): String = {
    current.configuration.getString("recaptcha.privatekey").get 
  }
  
  def check(addr: String, challenge: String, response: String): Boolean = {
    val reCaptcha = new ReCaptchaImpl()
    reCaptcha.setPrivateKey(privateKey())
    val reCaptchaResponse = reCaptcha.checkAnswer(addr, challenge, response)
    reCaptchaResponse.isValid()
  }
}
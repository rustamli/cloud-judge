
import cj_util
import re
import logging

from ctrl_base import Page
from datetime import datetime
from model.base import *
from google.appengine.ext import db

class SignInPage(Page):
    def get_template_name(self):
        return "sign-in"

    def post(self):
        email = self.request.get("email").strip()
        password = self.request.get("password")

        q = Account.all()
        q.filter("email =", email)
        account = q.get()

        if account is None:
            self.response.set_status(412)
            self.response.write("ACCOUNT_NOT_FOUND")
        elif cj_util.check_password(password, account.password):
            self.session["account"] = str(account.key())
            self.response.write("/accounts/" + str(account.key()))
        else:
            self.response.set_status(412)
            self.response.write("WRONG_PWD")


class SignUpPage(Page):
    def get_template_name(self):
        return "sign-up"

    def post(self):
        email = self.request.get("email").strip()
        password = self.request.get("password")
        password2 = self.request.get("password2")

        if not re.match(r"[^@]+@[^@]+\.[^@]+", email):
            self.response.set_status(412)
            self.response.write("BAD_EMAIL_FORMAT")
        elif password != password2:
            self.response.set_status(412)
            self.response.write("PWD_DONT_MATCH")
        elif len(password) < 5:
            self.response.set_status(412)
            self.response.write("PWD_TOO_SHORT")
        else:
            q = Account.all()
            q.filter("email =", email)
            existing_account = q.get()

            if existing_account:
                self.response.set_status(412)
                self.response.write("USER_EXISTS")
            else:
                account = Account(email = email,
                                  password = cj_util.enc_password(password),
                                  sign_up_date = datetime.now(),
                                  verified = False)
                account.put()

                self.session["account"] = str(account.key())
                self.response.write("/accounts/" + str(account.key()))

class AccountPage(Page):
    def get_template_name(self):
        return "account"

    def get(self, key):
        self.render_page({ "key": key })

    def get_template_values(self, lang, path_params, current_account):
        account = db.get(path_params["key"])

        return {
            "lang": lang,
            "email": account.email,
            "account": cj_util.get_current_account_dict(current_account)
        }

class SignOutHandler(Page):
    def get(self):
        self.session["account"] = None
        self.redirect("/")



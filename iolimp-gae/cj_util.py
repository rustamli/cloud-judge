
import os
import jinja2
import hashlib
import uuid


JINJA_ENVIRONMENT = jinja2.Environment(
    loader=jinja2.FileSystemLoader(os.path.dirname(__file__)),
    extensions=["jinja2.ext.autoescape"],
    autoescape=True)

def resolve_lang(page):
    switch_to_lang = page.request.get("lang")

    if switch_to_lang:
        page.session["lang"] = switch_to_lang
        page.redirect(page.request.path)

    lang = page.session.get("lang") or "az"

    return lang

def output(page, template_name, lang, template_values):
    template = JINJA_ENVIRONMENT.get_template("lang/" + lang + "/tpl/" + template_name + ".html")
    page.response.write(template.render(template_values))


def enc_password(raw_password):
    salt = uuid.uuid4().hex
    return salt + '$' + hashlib.sha512(raw_password + salt).hexdigest()

def check_password(raw_password, enc_password):
    salt, hsh = enc_password.split('$')
    return hashlib.sha512(raw_password + salt).hexdigest() == hsh

def get_current_account_dict(account):
    result = {}
    if account:
        result = {
            "email": account.email,
            "key": str(account.key())
        }

    return result

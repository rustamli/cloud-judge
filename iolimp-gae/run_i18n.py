__author__ = 'turan@rustam.li'

import yaml
from mako.template import Template

import hashlib
import uuid

COMMONS = {}

def run(lang, page):
    data = {}

    yaml_file_name = "lang/" + lang + "/data/" + page + ".yaml"

    try:
        f = open(yaml_file_name)
        data = yaml.safe_load(f)
        f.close()
    except IOError:
        print "yaml file not found: " + yaml_file_name

    if data is None:
        data = {}

    context = dict(data.items() + COMMONS[lang].items())

    f = open("tpl/" + page + ".tpl.html")
    template_src = f.read()
    f.close()

    f = open("lang/" + lang + "/tpl/" + page + ".html", "w")
    f.write(Template(template_src, default_filters=["decode.utf8"], input_encoding='utf-8',
                     output_encoding='utf-8',).render(**context))
    f.close()
    pass

def read_commons(lang):
    data = {}
    yaml_file_name = "lang/" + lang + "/data/common.yaml"
    try:
        f = open(yaml_file_name)
        data = yaml.safe_load(f)
        f.close()
    except IOError:
        print "yaml file not found: " + yaml_file_name

    return data


def build_all():
    pages = ["index", "sign-in", "sign-up", "account", "header"]

    COMMONS["az"] = read_commons("az")
    COMMONS["ru"] = read_commons("ru")
    COMMONS["en"] = read_commons("en")

    for page in pages:
        run("en", page)
        run("az", page)
        run("ru", page)

build_all()
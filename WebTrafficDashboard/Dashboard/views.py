# Create your views here.

from django.http.response import HttpResponse
from django.template import RequestContext
from django.shortcuts import render_to_response as Render



def index(request):

        context = {
                    'saludo':'HELLO WORLD',
                    'rmacario':'172.22.23.213',
                   }
        return Render('index.html',RequestContext(request,context))

def prueba(request):

        context = {
                    'saludo':'HELLO WORLD',
                    'rmacario':'172.22.23.213',
                   }
        return Render('dashboard.html',RequestContext(request,context))
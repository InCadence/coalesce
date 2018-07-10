# -*- coding: utf-8 -*-
"""
@author: sorr
"""

from sys import stdout, stderr
from time import sleep
from random import randint

from requests import Session, Request, ConnectionError, Timeout, ConnectTimeout

from pyCoalesce import package_logger


# Set up logging.
logger = package_logger.getChild(__name__)

# Create a session object.
session = Session()


def get_response(URL, method = "get", params = None, data = None,
                 headers = None, delay = 1, max_attempts = 8):
    """
    Make a RESTful API request with exponential backoff, and return
    the response.

    :param URL:  the URL of the service to be called
    :param method:  the HTML method for making the request
    :param params:  the parameters of the request
    :param data:  the data payload of the request
    :param headers:  the headers of the request
    :parama delay:  the delay for the first retry.  Later retries
        increase the delay by factors of 2.
    :params max_attempts:  the maximum number of times to attempt
        the request

    :return:  whatever response is returned for the request
    """

    # Create a dict of keyword arguments (this allows optional arguments
    # to be omitted entirely.
    req_kwargs = {}
    status = None
    for kwarg_key in ("params", "data", "headers"):
        kwarg_value = locals()[kwarg_key]
        if kwarg_value is not None:
            req_kwargs[kwarg_key] = kwarg_value

    # Create request objects.  Doing it this way, rather than with
    # "requests.request" (or ".get" or ".post"), lets us pass the request
    # object as an argument when raising a ConnectTimeout.  Using a
    # persistent session object (which is required for this approach)
    # should also have peformance advantages.
    request = Request(method = method, url = URL, **req_kwargs)
    prepped_req = request.prepare()

    # Call the API, using exponential backoff.
    for attempt in xrange(max_attempts):

        try:
            response = session.send(prepped_req)
            status = response.status_code
            if 200 <= status < 300:
                return response
            else:
                logger.warn('Server at ' + URL + ' returned status code "' +
                            str(status) + '".  Retrying....')
                stderr.flush()
        except (ConnectionError, Timeout) as err:
            logger.warn("Attempt to connect to " + URL + " failed with the " +
                        "following error message:\n" + str(err) +
                        "\nRetrying....")
            stderr.flush()

        fuzz = randint(1,1000) / 1000
        sleep(delay + fuzz)
        delay = 2 * delay

    # If no valid response is received, raise an error, with the status
    # code (if any) of the last response as the error message.
    if status:
        status_code = "status code " + str(status)
    else:
        status_code = "no response from server"
    err_msg = "Unable to complete request to " + URL + ":  " + status_code + "."
    raise ConnectTimeout(err_msg, request = request)


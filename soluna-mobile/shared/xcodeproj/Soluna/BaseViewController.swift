//
// Created by Russell Wolf on 9/12/19.
// Copyright (c) 2019 Kotlin/Native. All rights reserved.
//

import UIKit
import Shared

open class BaseViewController<VM : BaseViewModel<T>, T: AnyObject> : UIViewController {

    open var viewModel: VM? = nil

    let activityIndicator = UIActivityIndicatorView(style: .gray)

    open override func viewDidLoad() {
        super.viewDidLoad()

        configureActivityIndicator()

        viewModel?.setErrorListener { (error: KotlinThrowable) in
            error.printStackTrace()
        }

        viewModel?.setLoadingListener { (loading: KotlinBoolean) in
            if (loading as! Bool) {
                self.activityIndicator.startAnimating()
            } else {
                self.activityIndicator.stopAnimating()
            }
        }
    }

    private func configureActivityIndicator() {
        view.addSubview(activityIndicator)
        activityIndicator.hidesWhenStopped = true
        activityIndicator.translatesAutoresizingMaskIntoConstraints = false

        let horizontalConstraint = NSLayoutConstraint(item: activityIndicator, attribute: NSLayoutConstraint.Attribute.centerX, relatedBy: NSLayoutConstraint.Relation.equal, toItem: view, attribute: NSLayoutConstraint.Attribute.centerX, multiplier: 1, constant: 0)
        view.addConstraint(horizontalConstraint)

        let verticalConstraint = NSLayoutConstraint(item: activityIndicator, attribute: NSLayoutConstraint.Attribute.centerY, relatedBy: NSLayoutConstraint.Relation.equal, toItem: view, attribute: NSLayoutConstraint.Attribute.centerY, multiplier: 1, constant: 0)
        view.addConstraint(verticalConstraint)
    }
}

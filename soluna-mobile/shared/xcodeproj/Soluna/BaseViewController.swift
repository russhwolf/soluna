//
// Created by Russell Wolf on 9/12/19.
// Copyright (c) 2019 Kotlin/Native. All rights reserved.
//

import UIKit
import Shared

open class BaseViewController<VM: BaseViewModel<T>, T: AnyObject>: UIViewController {

    open func initViewModel() -> VM {
        preconditionFailure("BaseViewController subclass must override initViewModel()!")
    }

    lazy var viewModel: VM = initViewModel()

    let activityIndicator = UIActivityIndicatorView(style: .whiteLarge)
    lazy var activityIndicatorBackground = UIView(frame: view.frame)

    open override func viewDidLoad() {
        super.viewDidLoad()

        configureActivityIndicator()

        viewModel.setErrorListener { (error: KotlinThrowable) in
            error.printStackTrace()
        }

        viewModel.setLoadingListener { (loading: KotlinBoolean) in
            if (loading as! Bool) {
                self.activityIndicatorBackground.alpha = 1
                self.activityIndicator.startAnimating()
            } else {
                self.activityIndicator.stopAnimating()
                self.activityIndicatorBackground.alpha = 0
            }
        }
    }

    deinit {
        viewModel.clearScope()
    }

    private func configureActivityIndicator() {
        view.addSubview(activityIndicatorBackground)
        activityIndicatorBackground.backgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.5)

        view.addSubview(activityIndicator)
        activityIndicator.hidesWhenStopped = true
        activityIndicator.translatesAutoresizingMaskIntoConstraints = false

        let horizontalConstraint = NSLayoutConstraint(item: activityIndicator, attribute: .centerX, relatedBy: .equal, toItem: view, attribute: .centerX, multiplier: 1, constant: 0)
        view.addConstraint(horizontalConstraint)

        let verticalConstraint = NSLayoutConstraint(item: activityIndicator, attribute: .centerY, relatedBy: .equal, toItem: view, attribute: .centerY, multiplier: 1, constant: 0)
        view.addConstraint(verticalConstraint)
    }
}
